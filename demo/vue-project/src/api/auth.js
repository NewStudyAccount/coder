import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/login',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/register',
    method: 'post',
    data
  })
}

export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

export function logout() {
  // 由于我们使用 JWT，后端可能没有专门的退出接口（客户端清除 token 即可）
  // 但如果我们想通知后端做些什么，可以保留这个接口
  return Promise.resolve()
}
