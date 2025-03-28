import apiClient from './index'

const reviewApi = {

    getReviews() {
        return apiClient.get('/review', {
            headers: { 'Content-Type': 'application/json' },
        })
    },

    createReview(reviewData) {
        return apiClient.post('/review/submit', reviewData) 
    },

}

export default reviewApi
