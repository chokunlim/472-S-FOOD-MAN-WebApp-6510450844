<template>
    <main
        class="flex w-full h-screen bg-[url('../assets/bg2.png')] bg-no-repeat bg-cover"
    >
        <div
            class="flex justify-center items-center w-full mx-auto bg-white max-w-[100%] lg:max-w-[55%] md:max-w-[75%] h-full fixed right-0"
        >
            <form
                @submit.prevent="register"
                class="flex flex-col gap-8 p-8 w-[80%]"
            >
                <span class="font-extrabold text-3xl">Get Started</span>
                <div>
                    <span class="text-lg">Already have an account? </span>
                    <a class="text-blue-500" href="/signin">Login</a>
                </div>
                <input
                    v-model="username"
                    type="text"
                    placeholder="Username"
                    class="input"
                    required
                />
                <input
                    v-model="password"
                    type="password"
                    placeholder="Password"
                    class="input"
                    required
                />
                <input
                    v-model="confirmPassword"
                    type="password"
                    placeholder="Confirm Password"
                    class="input"
                    required
                />
                <!-- Added email input field -->
                <input
                    v-model="email"
                    type="email"
                    placeholder="Email"
                    class="input"
                    required
                />
                <input
                    v-model="phoneNumber"
                    type="text"
                    placeholder="Phone Number"
                    class="input"
                    required
                />
                <button type="submit" class="auth-button">Register</button>
                <div v-if="errorMessage" class="text-red-500">
                    {{ errorMessage }}
                </div>
            </form>
        </div>
    </main>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import authApi from '@/api/authApi'

const router = useRouter()

const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const email = ref('') // Added email ref
const phoneNumber = ref('')
const errorMessage = ref('')

const register = async () => {
    errorMessage.value = ''

    if (password.value !== confirmPassword.value) {
        errorMessage.value = 'Passwords do not match.'
        return
    }

    try {
        const { data: response } = await authApi.signUp({
            username: username.value,
            password: password.value,
            confirmPassword: confirmPassword.value,
            email: email.value, // Included email in the API call
            phone: phoneNumber.value,
        })

        router.push('/signin')

        console.log('Registration successful:', response.data)
    } catch (error) {
        console.error(
            'Registration failed:',
            error.response?.data || error.message
        )
        errorMessage.value = 'Registration failed. Please try again.'
    }
}
</script>

<style scoped>
.input {
    display: block;
    width: 100%;
    padding: 8px;
    margin-bottom: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
}

.auth-button {
    padding: 8px 16px;
    background-color: #4caf50;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.2s;
}

.auth-button:hover {
    background-color: #45a049;
}
</style>