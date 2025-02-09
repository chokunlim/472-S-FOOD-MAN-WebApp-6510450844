<template>
    <div class="review-card">
      <h2>Order Review</h2>
      <p><strong>Order ID:</strong> #{{ review.order.id.slice(0, 4) }}</p>
      <p><strong>Customer ID:</strong> #{{ review.customer.id.slice(0, 4) }}</p>
      <p><strong>Comment:</strong> {{ review.comment }}</p>
      <p><strong>Review Date:</strong> {{ review.createdAt }}</p>
      <p><strong>Rating:</strong> {{ review.rating }}</p>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      review: {
        type: Object,
        required: true,
      },
    },
  }
  </script>
  
  <style scoped>
  .review-card {
    border: 1px solid #ccc;
    padding: 16px;
    border-radius: 8px;
    max-width: 400px;
    margin: auto;
  }
  .review-card h2 {
    text-align: center;
  }
  .review-card p {
    margin: 8px 0;
  }
  .review-card button {
    margin-top: 12px;
    padding: 10px;
    background-color: #28a745;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  .review-card button:hover {
    background-color: #218838;
  }
  </style>