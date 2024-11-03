import apiClient from './index'

const userApi = {
    getUserByJwt() {
        return apiClient.get('/user/jwt', localStorage.getItem('token'), {
            headers: { 'Content-Type': 'application/json' },
        })
    },
}

export default userApi
