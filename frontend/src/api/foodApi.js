import apiClient from './index'

const foodApi = {
    getFoods() {
        return apiClient.get('/foods', {
            headers: { 'Content-Type': 'application/json' },
        })
    },

    createFood(foodData) {
        return apiClient.post('/foods', foodData) // multipart/form-data
    },
}

export default foodApi
