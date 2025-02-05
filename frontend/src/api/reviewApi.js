import apiClient from './index'

const reviewApi = {

    createReview(reviewData) {
        return apiClient.post('/reviews/submit', reviewData) 
    },
}

export default reviewApi
