<template>
  <aside class="fixed">
    <Sidebar />
  </aside>
  <main class="ml-[14rem] flex h-screen justify-center items-center">
    <div class="review-container w-full shadow-md h-3/4">
      <h2>Submit a Review</h2>
      <form @submit.prevent="submitReview" >
        <div>
          <label for="rating">Rating:</label>
          <fa
              v-for="i in 5"
              :key="i"
              icon="star"
              :class="i <= review.rating ? 'text-yellow-500' : 'text-gray-300'"
              @click="setRating(i)"
              class="cursor-pointer text-3xl"
          />
        </div>
        <div class="flex flex-col">
          <label for="comment">Comment:</label>
          <textarea v-model="review.comment" id="comment" rows="6"></textarea>
          <button class="shadow-md bg-yellow-300 px-4 py-2 buttom-0 rounded-lg" type="submit">Submit</button>
        </div>
      </form>
      <p>{{ message }}</p>
    </div>
  </main>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import reviewApi from "@/api/reviewApi.js";
import userApi from "@/api/userApi.js";
import Sidebar from "@/components/Sidebar.vue";
import router from '@/router';

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

const setRating = (rating) => {
  review.rating = rating
}

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

      router.push("/reviewlistview");

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
</script>

<style scoped>
.review-container {
max-width: 1000px;
min-width: 200px;
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