// 工具类函数集合

/**
 * 日期格式化
 * @param {Date|string|number} date
 * @param {string} fmt
 * @returns {string}
 */
export function formatDate(date, fmt = 'yyyy-MM-dd HH:mm:ss') {
  if (!date) return '';
  date = new Date(date);
  const o = {
    'M+': date.getMonth() + 1, // 月份
    'd+': date.getDate(), // 日
    'H+': date.getHours(), // 小时
    'm+': date.getMinutes(), // 分
    's+': date.getSeconds(), // 秒
    'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
    S: date.getMilliseconds() // 毫秒
  };
  let result = fmt;
  if (/(y+)/.test(fmt)) {
    result = result.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
  }
  for (const k in o) {
    if (new RegExp('(' + k + ')').test(result)) {
      result = result.replace(RegExp.$1, RegExp.$1.length === 1 ? o[k] : ('00' + o[k]).substr(('' + o[k]).length));
    }
  }
  return result;
}

/**
 * 深拷贝
 * @param {any} obj
 * @returns {any}
 */
export function deepClone(obj) {
  return JSON.parse(JSON.stringify(obj));
}

/**
 * 数组去重
 * @param {Array} arr
 * @returns {Array}
 */
export function uniqueArray(arr) {
  return Array.from(new Set(arr));
}

/**
 * 判断空对象
 * @param {Object} obj
 * @returns {boolean}
 */
export function isEmptyObject(obj) {
  return Object.keys(obj).length === 0 && obj.constructor === Object;
}

/**
 * 字符串首字母大写
 * @param {string} str
 * @returns {string}
 */
export function capitalize(str) {
  if (!str) return '';
  return str.charAt(0).toUpperCase() + str.slice(1);
}