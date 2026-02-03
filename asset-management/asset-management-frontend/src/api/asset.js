import request from '@/utils/request'

// 获取资产列表
export const getAssetList = (params) => {
  return request({
    url: '/assets',
    method: 'get',
    params
  })
}

// 获取资产详情
export const getAssetDetail = (id) => {
  return request({
    url: `/assets/${id}`,
    method: 'get'
  })
}

// 添加资产
export const addAsset = (data) => {
  return request({
    url: '/assets',
    method: 'post',
    data
  })
}

// 更新资产
export const updateAsset = (id, data) => {
  return request({
    url: `/assets/${id}`,
    method: 'put',
    data
  })
}

// 删除资产
export const deleteAsset = (id) => {
  return request({
    url: `/assets/${id}`,
    method: 'delete'
  })
}

// 批量删除资产
export const batchDeleteAssets = (ids) => {
  return request({
    url: '/assets/batch',
    method: 'delete',
    data: { ids }
  })
}

// 导出资产
export const exportAssets = (params) => {
  return request({
    url: '/assets/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取资产统计信息
export const getAssetStatistics = () => {
  return request({
    url: '/assets/statistics',
    method: 'get'
  })
}

// 获取资产分类列表
export const getCategoryList = (params) => {
  return request({
    url: '/categories',
    method: 'get',
    params
  })
}

// 获取资产分类树
export const getCategoryTree = () => {
  return request({
    url: '/categories/tree',
    method: 'get'
  })
}

// 添加资产分类
export const addCategory = (data) => {
  return request({
    url: '/categories',
    method: 'post',
    data
  })
}

// 更新资产分类
export const updateCategory = (id, data) => {
  return request({
    url: `/categories/${id}`,
    method: 'put',
    data
  })
}

// 删除资产分类
export const deleteCategory = (id) => {
  return request({
    url: `/categories/${id}`,
    method: 'delete'
  })
}
