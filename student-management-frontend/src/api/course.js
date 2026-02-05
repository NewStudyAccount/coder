import request from '@/utils/request'

/**
 * 课程管理相关API
 */

// 获取课程列表
export function getCourseList(params) {
  return request({
    url: '/courses',
    method: 'get',
    params
  })
}

// 获取课程详情
export function getCourseById(id) {
  return request({
    url: `/courses/${id}`,
    method: 'get'
  })
}

// 添加课程
export function addCourse(data) {
  return request({
    url: '/courses',
    method: 'post',
    data
  })
}

// 更新课程信息
export function updateCourse(id, data) {
  return request({
    url: `/courses/${id}`,
    method: 'put',
    data
  })
}

// 删除课程
export function deleteCourse(id) {
  return request({
    url: `/courses/${id}`,
    method: 'delete'
  })
}

// 获取课程的学生列表
export function getCourseStudents(courseId, params) {
  return request({
    url: `/courses/${courseId}/students`,
    method: 'get',
    params
  })
}

// 获取所有课程（用于下拉选择）
export function getAllCourses() {
  return request({
    url: '/courses/all',
    method: 'get'
  })
}

// 搜索课程
export function searchCourses(keyword) {
  return request({
    url: '/courses/search',
    method: 'get',
    params: { keyword }
  })
}
