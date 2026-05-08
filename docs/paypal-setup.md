# PayPal Credentials Setup
This guide is for retrieving and filling in the environment variables related to PayPal purchases needed to run the
application.

## Prerequisites
You need an existing PayPal account.
- If running in **development** mode, you can use either a _personal_ or _business_ account.
- If running in **production**, you _must_ use a business account.

> [!CAUTION]
> PayPal might flag / deactivate your account temporarily if you are using the developer dashboard for the first time. 
> This process is automated. Handle at your own discretion.

## Setup
### 1. Login to the developer portal
- Navigate to https://developer.paypal.com/home/
- Click **Log In**
    - Use the account that will receive payments

### 2. Open the Developer Dashboard
- In the top right, click **Go to Dashboard** > **Developer Dashboard**

### 3. Switch to the correct environment
At the top left of the dashboard, you will see:
- **Sandbox**
- **Live**

Developer builds will use **Sandbox**, while production will use **Live**. Switch to the correct environment if needed.

### 4. Create a new REST API app
On the left navigation bar, click **Apps & Credentials**.
- Click **Create App**
- Give it a name (e.g., "Yell Records Checkout")
- Make sure the type is **Merchant**
- Click **Create App**

This generates your credentials.

### 5. Copy your Client ID and Secret key
After creating the app, you will see:
- **Client ID**
- **Secret key 1** (hidden until shown)

These are the values the backend needs to run. Keep these values safe and do not share them publicly.

### 6. Add them to your environment variables
In your system environment:
```
PAYPAL_CLIENT_ID=your-client-id
PAYPAL_CLIENT_SECRET=your-secret-key
```

This concludes the setup for PayPal purchases.
