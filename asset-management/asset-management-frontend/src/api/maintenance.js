import request from '@/utils/request'

// 获取维护记录列表
export const getMaintenanceList = (params) => {
  return request({
    url: '/maintenances',
    method: 'get',
    params
  })
}

// 获取维护记录详情
export const getMaintenanceDetail = (id) => {
  return request({
    url: `/maintenances/${id}`,
    method: 'get'
  })
}

// 添加维护记录
export const addMaintenance = (data) => {
  return request({
    url: '/maintenances',
    method: 'post',
    data
  })
}

// 更新维护记录
export const updateMaintenance = (id, data) => {
  return request({
    url: `/maintenances/${id}`,
    method: 'put',
    data
  })
}

// 删除维护记录
export const deleteMaintenance = (id) => {
  return request({
    url: `/maintenances/${id}`,
    method: 'delete'
  })
}

// 获取资产的维护历史
export const getAssetMaintenanceHistory = (assetId) => {
  return request({
    url: `/maintenances/asset/${assetId}`,
    method: 'get'
  })
}
