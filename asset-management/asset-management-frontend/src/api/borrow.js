import request from '@/utils/request'

// 获取借用记录列表
export const getBorrowList = (params) => {
  return request({
    url: '/borrows',
    method: 'get',
    params
  })
}

// 获取借用记录详情
export const getBorrowDetail = (id) => {
  return request({
    url: `/borrows/${id}`,
    method: 'get'
  })
}

// 申请借用资产
export const applyBorrow = (data) => {
  return request({
    url: '/borrows',
    method: 'post',
    data
  })
}

// 审批借用申请
export const approveBorrow = (id, data) => {
  return request({
    url: `/borrows/${id}/approve`,
    method: 'post',
    data
  })
}

// 归还资产
export const returnAsset = (id, data) => {
  return request({
    url: `/borrows/${id}/return`,
    method: 'post',
    data
  })
}

// 获取我的借用记录
export const getMyBorrows = (params) => {
  return request({
    url: '/borrows/my',
    method: 'get',
    params
  })
}

// 获取逾期记录
export const getOverdueBorrows = (params) => {
  return request({
    url: '/borrows/overdue',
    method: 'get',
    params
  })
}
