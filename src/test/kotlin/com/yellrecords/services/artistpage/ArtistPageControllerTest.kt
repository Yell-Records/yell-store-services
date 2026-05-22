package com.yellrecords.services.artistpage

import com.yellrecords.services.BaseH2Test
import com.yellrecords.services.artistpage.dto.ArtistPageDto
import com.yellrecords.services.artistpage.dto.CreateArtistPageDto
import com.yellrecords.services.artistpage.dto.UpdateArtistPageDto
import com.yellrecords.services.category.Category
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.PATCH
import org.springframework.http.HttpMethod.POST
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.module.kotlin.readValue

class ArtistPageControllerTest : BaseH2Test() {
    companion object {
        private const val BASE_PATH = "/api/artist-pages"
    }

    @Autowired lateinit var artistPageRepository: ArtistPageRepository

    private lateinit var sampleCategory: Category

    @BeforeEach
    fun initialize() {
        super.initListings()

        sampleCategory = categoryRepository.findCategoryBySlug("sample-category")!!
    }

    @Nested
    inner class CreateArtistPage {
        private lateinit var createReq: CreateArtistPageDto

        @BeforeEach
        fun init() {
            createReq =
                CreateArtistPageDto(
                    name = "Test Artist",
                    slug = "test-artist",
                    bodyHtml = "<p><b>Test Artist</b></p>",
                    categorySlug = sampleCategory.slug,
                )
        }

        @Test
        fun `should return 403 forbidden for unauthenticated request`() {
            mockRequest(requestType = POST, path = BASE_PATH, token = null, body = createReq)
                .andExpect(status().isForbidden)
        }

        @Test
        fun `should create artist page`() {
            artistPageRepository.findAll().shouldBeEmpty()

            mockRequest(
                requestType = POST,
                path = BASE_PATH,
                token = TestTokens.admin,
                body = createReq,
            ).andExpect(status().isCreated)

            val artistPage = artistPageRepository.findArtistPageBySlug(createReq.slug)
            artistPage.shouldNotBeNull()
        }

        @Test
        fun `should return 400 bad request on invalid slug`() {
            val req = createReq.copy(slug = "INVALID SLUG !!!")

            mockRequest(requestType = POST, path = BASE_PATH, token = TestTokens.admin, body = req)
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `should return 409 conflict on duplicate slug`() {
            val entity =
                ArtistPage(
                    name = "Existing Artist",
                    slug = createReq.slug,
                    bodyHtml = "<p>Works</p>",
                    categoryId = sampleCategory.id!!,
                )

            artistPageRepository.save(entity)

            mockRequest(
                requestType = POST,
                path = BASE_PATH,
                token = TestTokens.admin,
                body = createReq,
            ).andExpect(status().isConflict)
        }
    }

    @Nested
    inner class GetArtistPage {
        private lateinit var sampleArtist1: ArtistPage

        @BeforeEach
        fun initialize() {
            val entity1 =
                ArtistPage(
                    name = "First Artist",
                    slug = "sample-slug",
                    bodyHtml = "<p>Works</p>",
                    categoryId = sampleCategory.id!!,
                )

            sampleArtist1 = artistPageRepository.save(entity1)

            val entity2 =
                ArtistPage(
                    name = "Second Artist",
                    slug = "better-slug",
                    bodyHtml = "<p>Works</p>",
                    categoryId = sampleCategory.id!!,
                )

            sampleArtist1 = artistPageRepository.save(entity2)
        }

        @Test
        fun `should get all artist pages`() {
            val result =
                mockRequest(requestType = GET, path = BASE_PATH, token = null)
                    .andExpect(status().isOk)
                    .andReturn()

            val body = result.response.contentAsString
            val pages = objectMapper.readValue<List<ArtistPageDto>>(body)

            pages shouldHaveSize 2
        }

        @Test
        fun `should get artist page by slug`() {
            val result =
                mockRequest(
                    requestType = GET,
                    path = "$BASE_PATH/slug/${sampleArtist1.slug}",
                    token = null,
                ).andExpect(status().isOk)
                    .andReturn()

            val body = result.response.contentAsString
            val artistPage = objectMapper.readValue<ArtistPageDto>(body)

            artistPage.id shouldBe sampleArtist1.id!!
        }

        @Test
        fun `should return 404 not found on non-existent slug`() {
            val badSlug = "non-existent-slug"

            mockRequest(requestType = GET, path = "$BASE_PATH/slug/$badSlug", token = null)
                .andExpect(status().isNotFound)
        }
    }

    @Nested
    inner class UpdateArtistPage {
        private lateinit var samplePage: ArtistPage

        @BeforeEach
        fun initialize() {
            val entity =
                ArtistPage(
                    name = "Some Artist",
                    slug = "sample-slug",
                    bodyHtml = "<p>Works</p>",
                    categoryId = sampleCategory.id!!,
                )

            samplePage = artistPageRepository.save(entity)
        }

        @Test
        fun `should return 403 forbidden on unauthenticated request`() {
            val updateReq = UpdateArtistPageDto(slug = "vulgar-slug")

            mockRequest(
                requestType = PATCH,
                path = "$BASE_PATH/${samplePage.id}",
                token = null,
                body = updateReq,
            ).andExpect(status().isForbidden)
        }

        @Test
        fun `should update artist page information`() {
            val updateReq =
                UpdateArtistPageDto(bodyHtml = "<h1>Some Artist</h1><p>More detailed html</p>")

            mockRequest(
                requestType = PATCH,
                path = "$BASE_PATH/${samplePage.id}",
                token = TestTokens.admin,
                body = updateReq,
            ).andExpect(status().isOk)

            val updatedEntity = artistPageRepository.findArtistPageBySlug(samplePage.slug)!!
            updatedEntity.bodyHtml shouldBe updateReq.bodyHtml
        }

        @Test
        fun `should return 204 no content on request with no changes`() {
            mockRequest(
                requestType = PATCH,
                path = "$BASE_PATH/${samplePage.id}",
                token = TestTokens.admin,
                body = UpdateArtistPageDto(),
            ).andExpect(status().isNoContent)
        }
    }

    @Nested
    inner class DeleteArtistPage {
        private lateinit var samplePage: ArtistPage

        @BeforeEach
        fun initialize() {
            val entity =
                ArtistPage(
                    name = "Some Artist",
                    slug = "sample-slug",
                    bodyHtml = "<p>Works</p>",
                    categoryId = sampleCategory.id!!,
                )

            samplePage = artistPageRepository.save(entity)
        }

        @Test
        fun `should return 403 forbidden on unauthenticated request`() {
            mockRequest(requestType = DELETE, path = "$BASE_PATH/${samplePage.id}", token = null)
                .andExpect(status().isForbidden)
        }

        @Test
        fun `should delete artist page`() {
            artistPageRepository.findArtistPageBySlug(samplePage.slug).shouldNotBeNull()

            mockRequest(
                requestType = DELETE,
                path = "$BASE_PATH/${samplePage.id}",
                token = TestTokens.admin,
            ).andExpect(status().isNoContent)

            artistPageRepository.findArtistPageBySlug(samplePage.slug).shouldBeNull()
        }
    }
}
