import apiClient from './index'

const promotionApi = {
    // สร้างโปรโมชั่นใหม่
    createPromotion(formData) {
        return apiClient.post('/promotions', formData) // multipart ไม่ต้องใส่ type
    },

    // ดึงโปรโมชั่นทั้งหมด
    getAllPromotions() {
        return apiClient.get('/promotions', {
            header: {
                'Content-Type': 'application/json',
            },
        })
    },

    // ดึงโปรโมชั่นตาม ID
    getPromotionById(id) {
        return apiClient.get(`/promotions/${id}`, {
            header: {
                'Content-Type': 'application/json',
            },
        })
    },

    // ลบโปรโมชั่น
    deletePromotion(id) {
        return apiClient.delete('/promotions', {
            data: id,
            header: {
                'Content-Type': 'application/json',
            },
        })
    },
}

export default promotionApi
