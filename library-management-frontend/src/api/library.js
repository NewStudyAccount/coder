import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 根据后端返回的code判断
    if (res.code === 200 || res.code === 0) {
      return res
    } else {
      ElMessage.error(res.message || res.msg || '请求失败')
      return Promise.reject(new Error(res.message || res.msg || '请求失败'))
    }
  },
  error => {
    console.error('响应错误:', error)
    
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('未授权，请重新登录')
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          router.push('/login')
          break
        case 403:
          ElMessage.error('拒绝访问')
          break
        case 404:
          ElMessage.error('请求错误，未找到该资源')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error(error.response.data.message || '请求失败')
      }
    } else {
      ElMessage.error('网络连接失败，请检查网络')
    }
    
    return Promise.reject(error)
  }
)

// ========== 用户相关API ==========

/**
 * 用户登录
 * @param {Object} data - 登录信息 {username, password}
 */
export const login = (data) => {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

/**
 * 用户注册
 * @param {Object} data - 注册信息 {username, password, email, phone}
 */
export const register = (data) => {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

/**
 * 获取用户信息
 */
export const getUserInfo = () => {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

/**
 * 用户退出登录
 */
export const logout = () => {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}

// ========== 图书相关API ==========

/**
 * 获取图书列表
 * @param {Object} params - 查询参数 {page, size, keyword, category, status}
 */
export const getBookList = (params) => {
  return request({
    url: '/book/list',
    method: 'get',
    params
  })
}

/**
 * 获取图书详情
 * @param {Number} id - 图书ID
 */
export const getBookDetail = (id) => {
  return request({
    url: `/book/${id}`,
    method: 'get'
  })
}

/**
 * 添加图书
 * @param {Object} data - 图书信息
 */
export const addBook = (data) => {
  return request({
    url: '/book/add',
    method: 'post',
    data
  })
}

/**
 * 更新图书信息
 * @param {Object} data - 图书信息
 */
export const updateBook = (data) => {
  return request({
    url: '/book/update',
    method: 'put',
    data
  })
}

/**
 * 删除图书
 * @param {Number} id - 图书ID
 */
export const deleteBook = (id) => {
  return request({
    url: `/book/${id}`,
    method: 'delete'
  })
}

/**
 * 获取图书分类列表
 */
export const getBookCategories = () => {
  return request({
    url: '/book/categories',
    method: 'get'
  })
}

// ========== 借阅相关API ==========

/**
 * 借阅图书
 * @param {Object} data - 借阅信息 {bookId, userId}
 */
export const borrowBook = (data) => {
  return request({
    url: '/borrow/create',
    method: 'post',
    data
  })
}

/**
 * 归还图书
 * @param {Number} borrowId - 借阅记录ID
 */
export const returnBook = (borrowId) => {
  return request({
    url: `/borrow/return/${borrowId}`,
    method: 'put'
  })
}

/**
 * 获取借阅记录列表
 * @param {Object} params - 查询参数 {page, size, userId, status}
 */
export const getBorrowList = (params) => {
  return request({
    url: '/borrow/list',
    method: 'get',
    params
  })
}

/**
 * 获取当前用户的借阅记录
 * @param {Object} params - 查询参数 {page, size, status}
 */
export const getMyBorrows = (params) => {
  return request({
    url: '/borrow/my',
    method: 'get',
    params
  })
}

/**
 * 获取借阅详情
 * @param {Number} id - 借阅记录ID
 */
export const getBorrowDetail = (id) => {
  return request({
    url: `/borrow/${id}`,
    method: 'get'
  })
}

/**
 * 续借图书
 * @param {Number} borrowId - 借阅记录ID
 */
export const renewBook = (borrowId) => {
  return request({
    url: `/borrow/renew/${borrowId}`,
    method: 'put'
  })
}

// ========== 统计相关API ==========

/**
 * 获取系统统计数据
 */
export const getStatistics = () => {
  return request({
    url: '/statistics/overview',
    method: 'get'
  })
}

/**
 * 获取热门图书排行
 * @param {Number} limit - 返回数量
 */
export const getPopularBooks = (limit = 10) => {
  return request({
    url: '/statistics/popular-books',
    method: 'get',
    params: { limit }
  })
}

export default request
