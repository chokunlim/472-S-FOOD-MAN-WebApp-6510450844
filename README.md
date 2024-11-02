# ผู้พัฒนา

1. ยศวัจน์ บวรศรีธนนนท์ 6510450879
2. ปริยวิศว์ เตชะกฤตเมธีธำรง 6510450593
3. ภูรี ลิ้มวงศ์รุจิรัตน์ 6510450844

# การติดตั้ง

## Download Project

```
git clone https://github.com/kirby-cs-org/restaurant.git
```

## Frontend

This template should help get you started developing with Vue 3 in Vite.

### Project Setup

```sh
npm install
```

### Compile and Hot-Reload for Development

```sh
npm run dev
```

### Compile and Minify for Production

```sh
npm run build
```

## Backend

ในส่วน Backend (restaurant) ติดตั้ง JDK Version 21

1. Database Name = sa2
2. Backend Port = 8088
3. Secret Token JWT (for test)

```sh
ffc0d02ab13cf1b58ac77b6165b17d6654837e8c845e197774754a0efd375560
```

4. Stripe API Key (for test)

```sh
sk_test_51Q6VTPBb2nJBY3WlwqNj5Uu2qphozXdNlD8AREPgcMYoFmDlMV7lNmgqBSjOxJKM7mWZ6uvVQR8DUbHFQcWomM2i009gCYwWNX
```

# Features

1. <b>Order Management</b>
   Allows for creating, updating, and tracking customer orders.
   Integrates order items with recipes and ingredients to update stock levels automatically.
   Supports marking orders as successful or cancelled.

   - `GET /order - Retrieve a list of orders.`

   - `POST /order - Create a new order.`
   - `PATCH /order/:id - Update the status of an order by ID.`

2. <b>Receipt Generation</b>
   Automatically generates receipts for completed orders.
   Includes details like itemized list, prices, and total cost.
   Can be printed or saved for accounting purposes.

   - `GET /receipt - Fetch receipts for orders.`
   - `POST /receipt - Create a new receipt for an order.`

3. <b>Recipe Management</b>
   Define and manage recipes for each menu item.
   Associates recipes with ingredients to ensure proper stock management.
   Allows customization of ingredient quantities for flexible recipe variations.

   - `GET /recipe - Retrieve a list of recipes.`
   - `GET /recipe/:id - Retrieve recipe by id`
   - `PATCH /recipe/:id - Update an existing recipe by ID.`

4. <b>Ingredient Management</b>
   Manages the inventory of ingredients, tracking available quantities and expiration dates.
   Enables adding, editing, and updating ingredient information.
   Automatically reduces ingredient quantities based on order fulfillment.

   - `GET /ingredient - Retrieve a list of ingredients.`
   - `POST /ingredient - Add a new ingredient to inventory.`
   - `PATCH /ingredient/:id - Update quantity or status of an ingredient.`

5. <b>Food Menu Management</b>
   Manages the list of available food items and their prices.
   Provides options for enabling or disabling items based on stock or seasonal availability.
   Links food items to specific recipes and ingredients for streamlined ordering.

   - `GET /food - Retrieve the list of food items on the menu.`
   - `POST /food - Add a new item to the menu.`
   - `PATCH /food/:id - Update a menu item’s details.`

6. <b>User Authentication</b>
   Secure user sign-up and sign-in functionality with role-based access control.
   Admin access to manage inventory, orders, and menu items.
   General users have access to view menu items and place orders.

   - `GET /user/jwt - Get current user token`
   - `POST /auth/signup - Register a new user.`
   - `POST /auth/signin - Log in to an existing account.`

7. <b>Dashboard</b>
   Provides an overview of financial performance, including income, expenses, and net totals by date. Useful for tracking daily business metrics and for financial planning.

   - `GET /financial - Retrieve financial data, including income, expense, and total summaries by date.`
