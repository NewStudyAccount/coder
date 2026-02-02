<template>
  <div class="books-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="图书名称">
          <el-input
            v-model="searchForm.bookName"
            placeholder="请输入图书名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="作者">
          <el-input
            v-model="searchForm.author"
            placeholder="请输入作者"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="ISBN">
          <el-input
            v-model="searchForm.isbn"
            placeholder="请输入ISBN"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select
            v-model="searchForm.categoryId"
            placeholder="请选择分类"
            clearable
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="可借阅" value="available" />
            <el-option label="已借出" value="borrowed" />
            <el-option label="维护中" value="maintenance" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">图书列表</span>
          <div class="header-actions">
            <el-button
              v-if="userStore.userInfo?.role === 'admin'"
              type="primary"
              :icon="Plus"
              @click="handleAdd"
            >
              新增图书
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="bookList"
        style="width: 100%"
        stripe
        @row-click="handleRowClick"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="bookName" label="图书名称" min-width="200" />
        <el-table-column prop="author" label="作者" width="150" />
        <el-table-column prop="isbn" label="ISBN" width="150" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="publisher" label="出版社" width="150" />
        <el-table-column prop="publishDate" label="出版日期" width="120" />
        <el-table-column label="库存" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.availableCount > 0 ? 'success' : 'danger'">
              {{ row.availableCount }}/{{ row.totalCount }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 'available'" type="success">
              可借阅
            </el-tag>
            <el-tag v-else-if="row.status === 'borrowed'" type="warning">
              已借出
            </el-tag>
            <el-tag v-else type="info">维护中</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              :icon="View"
              @click.stop="handleView(row)"
            >
              查看
            </el-button>
            <el-button
              v-if="row.availableCount > 0"
              type="success"
              size="small"
              :icon="DocumentAdd"
              @click.stop="handleBorrow(row)"
            >
              借阅
            </el-button>
            <template v-if="userStore.userInfo?.role === 'admin'">
              <el-button
                type="warning"
                size="small"
                :icon="Edit"
                @click.stop="handleEdit(row)"
              >
                编辑
              </el-button>
              <el-button
                type="danger"
                size="small"
                :icon="Delete"
                @click.stop="handleDelete(row)"
              >
                删除
              </el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.currentPage"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 图书详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="图书详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-descriptions v-if="currentBook" :column="2" border>
        <el-descriptions-item label="图书ID">
          {{ currentBook.id }}
        </el-descriptions-item>
        <el-descriptions-item label="ISBN">
          {{ currentBook.isbn }}
        </el-descriptions-item>
        <el-descriptions-item label="图书名称" :span="2">
          {{ currentBook.bookName }}
        </el-descriptions-item>
        <el-descriptions-item label="作者">
          {{ currentBook.author }}
        </el-descriptions-item>
        <el-descriptions-item label="分类">
          {{ currentBook.categoryName }}
        </el-descriptions-item>
        <el-descriptions-item label="出版社" :span="2">
          {{ currentBook.publisher }}
        </el-descriptions-item>
        <el-descriptions-item label="出版日期">
          {{ currentBook.publishDate }}
        </el-descriptions-item>
        <el-descriptions-item label="价格">
          ¥{{ currentBook.price }}
        </el-descriptions-item>
        <el-descriptions-item label="总库存">
          {{ currentBook.totalCount }}
        </el-descriptions-item>
        <el-descriptions-item label="可借数量">
          {{ currentBook.availableCount }}
        </el-descriptions-item>
        <el-descriptions-item label="位置">
          {{ currentBook.location }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentBook.status === 'available'" type="success">
            可借阅
          </el-tag>
          <el-tag v-else-if="currentBook.status === 'borrowed'" type="warning">
            已借出
          </el-tag>
          <el-tag v-else type="info">维护中</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="简介" :span="2">
          {{ currentBook.description || '暂无简介' }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          v-if="currentBook?.availableCount > 0"
          type="primary"
          @click="handleBorrowFromDetail"
        >
          立即借阅
        </el-button>
      </template>
    </el-dialog>

    <!-- 新增/编辑图书对话框 -->
    <el-dialog
      v-model="formDialogVisible"
      :title="formMode === 'add' ? '新增图书' : '编辑图书'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="bookFormRef"
        :model="bookForm"
        :rules="bookFormRules"
        label-width="100px"
      >
        <el-form-item label="图书名称" prop="bookName">
          <el-input v-model="bookForm.bookName" placeholder="请输入图书名称" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="bookForm.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="ISBN" prop="isbn">
          <el-input v-model="bookForm.isbn" placeholder="请输入ISBN" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select
            v-model="bookForm.categoryId"
            placeholder="请选择分类"
            style="width: 100%"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="出版社" prop="publisher">
          <el-input v-model="bookForm.publisher" placeholder="请输入出版社" />
        </el-form-item>
        <el-form-item label="出版日期" prop="publishDate">
          <el-date-picker
            v-model="bookForm.publishDate"
            type="date"
            placeholder="请选择出版日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="bookForm.price"
            :min="0"
            :precision="2"
            placeholder="请输入价格"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="总库存" prop="totalCount">
          <el-input-number
            v-model="bookForm.totalCount"
            :min="1"
            placeholder="请输入总库存"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="bookForm.location" placeholder="请输入书架位置" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select
            v-model="bookForm.status"
            placeholder="请选择状态"
            style="width: 100%"
          >
            <el-option label="可借阅" value="available" />
            <el-option label="维护中" value="maintenance" />
          </el-select>
        </el-form-item>
        <el-form-item label="简介" prop="description">
          <el-input
            v-model="bookForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入图书简介"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 借阅对话框 -->
    <el-dialog
      v-model="borrowDialogVisible"
      title="借阅图书"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="borrowFormRef"
        :model="borrowForm"
        :rules="borrowFormRules"
        label-width="100px"
      >
        <el-form-item label="图书名称">
          <el-input v-model="currentBook?.bookName" disabled />
        </el-form-item>
        <el-form-item label="借阅天数" prop="borrowDays">
          <el-input-number
            v-model="borrowForm.borrowDays"
            :min="1"
            :max="90"
            placeholder="请输入借阅天数"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="预计归还">
          <el-input :value="expectedReturnDate" disabled />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="borrowForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="borrowDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="borrowLoading"
          @click="handleConfirmBorrow"
        >
          确认借阅
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Search,
  Refresh,
  Plus,
  View,
  Edit,
  Delete,
  DocumentAdd
} from '@element-plus/icons-vue'
import {
  getBookList,
  getBookDetail,
  addBook,
  updateBook,
  deleteBook,
  getBookCategories,
  borrowBook
} from '@/api/library'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

// 搜索表单
const searchForm = reactive({
  bookName: '',
  author: '',
  isbn: '',
  categoryId: '',
  status: ''
})

// 图书列表
const bookList = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 分类列表
const categories = ref([])

// 详情对话框
const detailDialogVisible = ref(false)
const currentBook = ref(null)

// 表单对话框
const formDialogVisible = ref(false)
const formMode = ref('add') // add | edit
const bookFormRef = ref(null)
const submitLoading = ref(false)

const bookForm = reactive({
  id: null,
  bookName: '',
  author: '',
  isbn: '',
  categoryId: '',
  publisher: '',
  publishDate: '',
  price: 0,
  totalCount: 1,
  location: '',
  status: 'available',
  description: ''
})

const bookFormRules = {
  bookName: [{ required: true, message: '请输入图书名称', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  isbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  publisher: [{ required: true, message: '请输入出版社', trigger: 'blur' }],
  publishDate: [{ required: true, message: '请选择出版日期', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  totalCount: [{ required: true, message: '请输入总库存', trigger: 'blur' }],
  location: [{ required: true, message: '请输入书架位置', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 借阅对话框
const borrowDialogVisible = ref(false)
const borrowFormRef = ref(null)
const borrowLoading = ref(false)

const borrowForm = reactive({
  bookId: null,
  borrowDays: 30,
  remark: ''
})

const borrowFormRules = {
  borrowDays: [
    { required: true, message: '请输入借阅天数', trigger: 'blur' },
    { type: 'number', min: 1, max: 90, message: '借阅天数必须在1-90天之间', trigger: 'blur' }
  ]
}

// 计算预计归还日期
const expectedReturnDate = computed(() => {
  if (!borrowForm.borrowDays) return ''
  const date = new Date()
  date.setDate(date.getDate() + borrowForm.borrowDays)
  return date.toLocaleDateString('zh-CN')
})

// 加载图书列表
const loadBookList = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize
    }
    const response = await getBookList(params)
    bookList.value = response.data.list || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('加载图书列表失败')
  } finally {
    loading.value = false
  }
}

// 加载分类列表
const loadCategories = async () => {
  try {
    const response = await getBookCategories()
    categories.value = response.data || []
  } catch (error) {
    console.error('加载分类列表失败', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  loadBookList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    bookName: '',
    author: '',
    isbn: '',
    categoryId: '',
    status: ''
  })
  handleSearch()
}

// 分页变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadBookList()
}

const handleCurrentChange = (page) => {
  pagination.currentPage = page
  loadBookList()
}

// 行点击
const handleRowClick = (row) => {
  handleView(row)
}

// 查看详情
const handleView = async (row) => {
  try {
    const response = await getBookDetail(row.id)
    currentBook.value = response.data
    detailDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载图书详情失败')
  }
}

// 新增
const handleAdd = () => {
  formMode.value = 'add'
  resetBookForm()
  formDialogVisible.value = true
}

// 编辑
const handleEdit = async (row) => {
  formMode.value = 'edit'
  try {
    const response = await getBookDetail(row.id)
    Object.assign(bookForm, response.data)
    formDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载图书信息失败')
  }
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该图书吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteBook(row.id)
    ElMessage.success('删除成功')
    loadBookList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!bookFormRef.value) return

  await bookFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      if (formMode.value === 'add') {
        await addBook(bookForm)
        ElMessage.success('新增成功')
      } else {
        await updateBook(bookForm.id, bookForm)
        ElMessage.success('更新成功')
      }
      formDialogVisible.value = false
      loadBookList()
    } catch (error) {
      ElMessage.error(formMode.value === 'add' ? '新增失败' : '更新失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 重置表单
const resetBookForm = () => {
  Object.assign(bookForm, {
    id: null,
    bookName: '',
    author: '',
    isbn: '',
    categoryId: '',
    publisher: '',
    publishDate: '',
    price: 0,
    totalCount: 1,
    location: '',
    status: 'available',
    description: ''
  })
  if (bookFormRef.value) {
    bookFormRef.value.resetFields()
  }
}

// 借阅
const handleBorrow = (row) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }

  currentBook.value = row
  borrowForm.bookId = row.id
  borrowForm.borrowDays = 30
  borrowForm.remark = ''
  borrowDialogVisible.value = true
}

// 从详情借阅
const handleBorrowFromDetail = () => {
  detailDialogVisible.value = false
  handleBorrow(currentBook.value)
}

// 确认借阅
const handleConfirmBorrow = async () => {
  if (!borrowFormRef.value) return

  await borrowFormRef.value.validate(async (valid) => {
    if (!valid) return

    borrowLoading.value = true
    try {
      await borrowBook({
        bookId: borrowForm.bookId,
        borrowDays: borrowForm.borrowDays,
        remark: borrowForm.remark
      })
      ElMessage.success('借阅成功')
      borrowDialogVisible.value = false
      loadBookList()
    } catch (error) {
      ElMessage.error('借阅失败')
    } finally {
      borrowLoading.value = false
    }
  })
}

// 初始化
onMounted(() => {
  loadCategories()
  loadBookList()
})
</script>

<style scoped>
.books-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: -18px;
}

.table-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 10px;
}

:deep(.el-table) {
  margin-bottom: 20px;
}

:deep(.el-table__row) {
  cursor: pointer;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

:deep(.el-pagination) {
  justify-content: center;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .books-container {
    padding: 10px;
  }

  .search-form {
    :deep(.el-form-item) {
      margin-bottom: 18px;
    }
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .header-actions {
    width: 100%;
  }

  :deep(.el-table) {
    font-size: 12px;
  }

  :deep(.el-dialog) {
    width: 90% !important;
  }
}
</style>
