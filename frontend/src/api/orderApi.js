import apiClient from './index'

const orderApi = {
    getOrders() {
        return apiClient.get('/order', {
            headers: { 'Content-Type': 'application/json' },
        })
    },

    // getOrderUserById(orderId) {
    //     return apiClient.get(`/order/${orderId}/user`, {
    //         headers: { 'Content-Type': 'application/json' },
    //     })
    // },

    createOrder(orderData) {
        return apiClient.post('/order', orderData, {
            headers: { 'Content-Type': 'application/json' },
        })
    },

    updateOrderStatus(orderStatus) {
        return apiClient.patch('/order', orderStatus, {
            headers: { 'Content-Type': 'application/json' },
        })
    },

    updateOrderIngredientQty(orderId) {
        return apiClient.post(`/order/ingredient/${orderId}`, {
            headers: { 'Content-Type': 'application/json' },
        })
    },

}

export default orderApi
