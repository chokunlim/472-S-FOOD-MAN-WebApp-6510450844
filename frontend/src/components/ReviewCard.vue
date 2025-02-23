<template>
  <div class="review-card">
    <h1 class="review-title">Customer Review</h1>
    <p class="review-date"><strong>Review Date:</strong> {{ review.createdAt }}</p>
    <p><strong>Order ID:</strong> #{{ review.order.id.slice(0, 4) }}</p>
    <p><strong>Customer ID:</strong> #{{ review.customer.id.slice(0, 4) }}</p>
    <p class="review-comment"><strong>Comment:</strong> {{ review.comment }}</p>
    <p class="review-rating"><strong>Rating:</strong> <span v-html="starRating"></span></p>
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
  computed: {
    starRating() {
      const filledStars = '★'.repeat(this.review.rating);
      const emptyStars = '☆'.repeat(5 - this.review.rating);
      return `<span class="stars">${filledStars}</span><span class="stars empty">${emptyStars}</span>`;
    },
  },
};
</script>

<style scoped>
.review-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  max-width: 400px;
  margin: auto;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease-in-out;
}

.review-card:hover {
  transform: translateY(-5px);
}

.review-title {
  text-align: center;
  font-size: 22px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.review-date {
  font-size: 14px;
  color: #666;
}

.review-card p {
  margin: 8px 0;
  font-size: 16px;
  color: #444;
}

.review-comment {
  font-size: 18px;
  color: #555;
}

.review-rating {
  font-size: 18px;
  font-weight: bold;
}

.stars {
  color: gold;
  font-size: 20px;
}

.stars.empty {
  color: #ddd;
}
</style>
