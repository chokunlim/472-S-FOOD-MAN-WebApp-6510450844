<script setup>
import { ref, onMounted, computed } from 'vue'
import Sidebar from '@/components/Sidebar.vue'
import ReviewCard from '@/components/ReviewCard.vue'
import userApi from '@/api/userApi'
import reviewApi from '@/api/reviewApi'
import router from '@/router'

const reviews = ref([])
const role = ref('')
const user = ref({})
const dropdownVisible = ref(false)
const selectedOption = ref('All Review')
const searchQuery = ref('')

const fetchReviews = async () => {
    try {
        const { data: reviewResponse } = await reviewApi.getReviews()
        const { data: userResponse } = await userApi.getUserByJwt()

        user.value = userResponse.data
        role.value = userResponse.data.role

        reviews.value = reviewResponse.data

        console.log('Filtered reviews for display:', reviews.value)
    } catch (error) {
        console.error('Error fetching reviews:', error)
    }
}

onMounted(() => {
    fetchReviews()
})

const toggleDropdown = () => {
    dropdownVisible.value = !dropdownVisible.value
}

const selectOption = (option) => {
    selectedOption.value = option
    dropdownVisible.value = false
}

const filteredReviews = computed(() => {
    return reviews.value.filter((review) => {
        const matchesStatus =
            selectedOption.value === 'All Review'
        const matchesSearch = searchQuery.value
            ? review.comment.toLowerCase().includes(searchQuery.value.toLowerCase())
            : true
        return matchesStatus && matchesSearch
    })
})

const handleViewDetail = (reviewId) => {
    router.push({ name: 'reviewDetail', params: { id: reviewId } })
}
</script>

<template>
    <div class="flex">
        <aside class="fixed">
            <Sidebar />
        </aside>
        <main
            class="ml-[14rem] w-full py-4 px-8 flex flex-col gap-4 bg-gray-50 h-screen"
        >
            <section class="w-full">
                <span class="font-bold text-3xl">Reviews</span>
            </section>

            <div class="relative inline-block mt-2">
                <button
                    @click="toggleDropdown"
                    class="flex px-2 cursor-pointer gap-2 text-gray-500"
                >
                    {{ selectedOption }}
                    <fa icon="sort-down" />
                </button>
                <ul
                    v-if="dropdownVisible"
                    class="absolute bg-white rounded-lg shadow-lg text-gray-500"
                >
                    <li
                        v-for="option in [
                            'All Review',
                        ]"
                        :key="option"
                        @click="selectOption(option)"
                        class="px-4 py-2 hover:bg-gray-100 cursor-pointer rounded-lg"
                    >
                        {{ option }}
                    </li>
                </ul>
            </div>

            <section class="mt-4">
                <ReviewCard
                    v-for="(review, i) in filteredReviews"
                    :index="i + 1"
                    :key="review.review_id"
                    :review="review"
                    @view-detail="handleViewDetail"
                />
            </section>
        </main>
    </div>
</template>