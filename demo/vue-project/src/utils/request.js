import axios from 'axios'

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api', // 这里使用 /api 前缀，需要在 vite.config.js 中配置代理
  timeout: 5000 // 请求超时时间
})

// request 拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    const token = localStorage.getItem('token')
    if (token) {
      // 让每个请求携带 token
      // Bearer 是 JWT 的标准前缀
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    // 对请求错误做些什么
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// response 拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    // 这里可以根据后端的通用返回格式进行处理
    // 例如，如果 code 不为 0，则判断为错误
    // 但目前我们的后端只有部分接口遵循 {code: 0}，登录接口直接返回 {token: ...}
    return res
  },
  error => {
    console.log('err' + error) // for debug
    return Promise.reject(error)
  }
)

export default service
