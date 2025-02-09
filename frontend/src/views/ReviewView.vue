<template>
    <div class="review-container">
      <h2>Submit a Review</h2>
      <form @submit.prevent="submitReview">
        <div>
          <label for="rating">Rating:</label>
          <select v-model="review.rating" id="rating">
            <option v-for="n in 5" :key="n" :value="n">{{ n }}</option>
          </select>
        </div>
        <div>
          <label for="comment">Comment:</label>
          <textarea v-model="review.comment" id="comment" rows="4"></textarea>
        </div>
        <button type="submit">Submit</button>
      </form>
      <p>{{ message }}</p>
    </div>
</template>

<script>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import reviewApi from "@/api/reviewApi";
import userApi from "@/api/userApi";

export default {
    setup() {
        const route = useRoute();
        const message = ref("");
        const review = reactive({
            reviewId: "",
            comment: "",
            reviewDate: "",
            rating: 1,
            customerId: "",
            orderId: route.params.id,
        });

        const submitReview = async () => {
            try {
                const { data: customer_res } = await userApi.getUserByJwt();
                review.customerId = customer_res.id;


                console.log(review.orderId);
                console.log(review.rating);
                console.log(review.comment);


                const { data: review_res } = await reviewApi.createReview({
                    orderId: review.orderId,
                    rating: review.rating,
                    comment: review.comment,
                });

                message.value = "Review submitted successfully!";
                console.log(review_res);
                console.log(customer_res);
                
            } catch (error) {
                message.value = "Error submitting review.";
                console.error("Error submitting review:", error);
            }
        };

        onMounted(async () => {
            try {
                const { data: customer_res } = await userApi.getUserByJwt();
                review.customerId = customer_res.id;
            } catch (error) {
                message.value = "Error fetching user data.";
                console.error("Error fetching user:", error);
            }
        });

        watch(
            () => route.params.id,
            (newId) => {
                review.orderId = newId;
            }
        );

        return {
            review,
            message,
            submitReview,
        };
    },
};
</script>

<style scoped>
.review-container {
  max-width: 400px;
  margin: auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 10px;
  background: #f9f9f9;
}
label {
  display: block;
  margin: 10px 0 5px;
}
textarea {
  width: 100%;
}
button {
  margin-top: 10px;
}
</style>