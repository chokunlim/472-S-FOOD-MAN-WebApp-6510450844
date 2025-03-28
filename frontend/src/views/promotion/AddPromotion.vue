<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import Sidebar from '@/components/Sidebar.vue'
import promotionApi from '@/api/promotionApi'
import foodApi from '@/api/foodApi'

const foods = reactive([])
const food = ref([])

type Promotion = {
    name: string
    description: string
    price: number
    startDate: string
    endDate: string
    foodIds: string[]
}

const form = reactive<Promotion>( {
    name: '',
    description: '',
    price: 0.0,
    startDate: new Date().toISOString().split('T')[0], // วันที่ปัจจุบันในรูปแบบ YYYY-MM-DD
    endDate: new Date().toISOString().split('T')[0], // วันที่ปัจจุบันในรูปแบบ YYYY-MM-DD
    foodIds: [],
})

const isSubmitting = ref(false) // เพิ่มตัวแปร isSubmitting เพื่อเช็คสถานะการทำงาน

async function getFoods() {
    try {
        const { data: res } = await foodApi.getFoods()
        foods.push(...res.data)
    } catch (err) {
        console.error(err)
    }
}

const imageFile = ref<File | null>(null)
const imagePreview = ref<string>('')

function handleImageUpload(event: Event) {
    const target = event.target as HTMLInputElement
    const file = target.files?.[0] || null
    imageFile.value = file

    if (file) {
        imagePreview.value = URL.createObjectURL(file)
    } else {
        imagePreview.value = ''
    }
}

const createPromotion = async () => {
    if (isSubmitting.value) return // หากกำลังส่งข้อมูลอยู่ให้หยุดการทำงาน

    if (!imageFile.value) {
        alert('Please upload an image.')
        return
    }

    try {
        isSubmitting.value = true // กำหนดสถานะเป็นกำลังส่งข้อมูล

        const formData = new FormData()
        formData.append(
            'promotion',
            new Blob([JSON.stringify(form)], {
                type: 'application/json',
            })
        )
        formData.append('image', imageFile.value)

        await promotionApi.createPromotion(formData)
        alert('Promotion added successfully!')
        router.push('/promotions')
    } catch (error) {
        console.error('Error adding foods:', error.response?.data)
    } finally {
        isSubmitting.value = false // เปลี่ยนสถานะกลับหลังจากส่งข้อมูลเสร็จ
    }
}

onMounted(() => {
    getFoods()
})
</script>

<template>
    <div class="flex">
        <aside class="fixed">
            <Sidebar />
        </aside>
        <main
            class="ml-[14rem] w-full py-4 px-8 flex flex-col gap-4 bg-gray-50 h-full min-h-screen"
        >
            <h2 class="text-xl font-bold">Create New Promotion</h2>

            <!-- Promotion Form -->
            <form @submit.prevent="createPromotion" class="flex flex-col gap-2">
                <input
                    v-model="form.name"
                    type="text"
                    placeholder="Promotion Name"
                    required
                    class="p-2 border rounded"
                />
                <input
                    v-model="form.description"
                    type="text"
                    placeholder="Description"
                    required
                    class="p-2 border rounded"
                />
                <input
                    v-model.number="form.price"
                    type="number"
                    placeholder="Discount Value"
                    required
                    class="p-2 border rounded"
                />
                <input
                    v-model="form.startDate"
                    type="date"
                    required
                    class="p-2 border rounded"
                />
                <input
                    v-model="form.endDate"
                    type="date"
                    required
                    class="p-2 border rounded"
                />
                <input
                    type="file"
                    accept="image/*"
                    @change="handleImageUpload"
                    class="p-2 border rounded"
                />

                <!-- Image Preview -->
                <div v-if="imagePreview" class="mt-4">
                    <img
                        :src="imagePreview"
                        alt="Image Preview"
                        class="w-32 h-32 object-cover"
                    />
                </div>

                <!-- Select Foods -->
                <label class="mt-4">Select Foods:</label>
                <div v-if="foods.length" class="flex flex-col gap-2">
                    <div v-for="food in foods" :key="food.id">
                        <input
                            type="checkbox"
                            :value="food.id"
                            v-model="form.foodIds"
                        />
                        <span>{{ food.name }} ({{ food.price }}$)</span>
                    </div>
                </div>
                <div v-else>
                    <p>Loading ingredients...</p>
                </div>

                <button
                    type="submit"
                    class="mt-4"
                    :disabled="isSubmitting" 
                >
                    Create Promotion
                </button>
            </form>
        </main>
    </div>
</template>

<style scoped>
input,
select {
    display: block;
    width: 100%;
    padding: 8px;
    margin-bottom: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
}

button {
    padding: 8px 16px;
    background-color: #4caf50;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.2s;
}

button:hover {
    background-color: #45a049;
}

button:disabled {
    background-color: #ddd;
    cursor: not-allowed;
}
</style>
