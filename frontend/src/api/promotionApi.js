import apiClient from './index'

const promotionApi = {
    // สร้างโปรโมชั่นใหม่
    createPromotion(formData) {
        return apiClient.post('/promotions', formData) // multipart ไม่ต้องใส่ type
    },

   // ✅ ดึงโปรโมชั่นทั้งหมด
   getAllPromotions() {
        return apiClient.get('/promotions', {
            headers: {
                'Content-Type': 'application/json',
            }, 
        })
    },

    // ✅ ดึงโปรโมชั่นตาม ID
    getPromotionById(id) {
        return apiClient.get(`/promotions/${id}`, {
            headers: {
                'Content-Type': 'application/json',
            },
        })
    },

    // ✅ ลบโปรโมชั่น (แก้ให้ส่ง id ใน URL)
    deletePromotion(id) {
        return apiClient.delete(`/promotions/${id}`, {
            headers: {
                'Content-Type': 'application/json',
            },
        })
    },

    // ✅ แก้ไขโปรโมชั่น (รองรับการอัปโหลดไฟล์)
    updatePromotion(id, formData) {
        return apiClient.put(`/promotions/${id}`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        })
    },
}

export default promotionApi
