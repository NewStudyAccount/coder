<template>
  <div class="oss-container">
    <h2>OSS 文件管理</h2>
    
    <!-- 上传区域 -->
    <div class="upload-section">
      <input type="file" @change="handleFileChange" />
      <button @click="uploadFile" :disabled="!selectedFile">上传文件</button>
    </div>

    <div v-if="message" :class="['message', messageType]">{{ message }}</div>

    <!-- 列表展示区域 -->
    <div class="file-list">
      <h3>文件列表</h3>
      <table>
        <thead>
          <tr>
            <th>文件名</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="file in files" :key="file">
            <td>{{ file }}</td>
            <td>
              <button @click="downloadFile(file)">下载</button>
              <button @click="deleteFile(file)" class="delete-btn">删除</button>
            </td>
          </tr>
          <tr v-if="files.length === 0">
            <td colspan="2" style="text-align: center;">暂无文件</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
export default {
  name: 'OssManage',
  data() {
    return {
      files: [],
      selectedFile: null,
      message: '',
      messageType: 'info',
      apiUrl: 'http://localhost:8080/api/oss' // 假设后端运行在 8080 端口
    };
  },
  mounted() {
    this.fetchFileList();
  },
  methods: {
    fetchFileList() {
      fetch(`${this.apiUrl}/list`)
        .then(response => response.json())
        .then(data => {
          this.files = data;
        })
        .catch(err => this.showMessage('获取列表失败', 'error'));
    },
    handleFileChange(event) {
      this.selectedFile = event.target.files[0];
    },
    uploadFile() {
      if (!this.selectedFile) return;
      
      const formData = new FormData();
      formData.append('file', this.selectedFile);

      fetch(`${this.apiUrl}/upload`, {
        method: 'POST',
        body: formData
      })
      .then(response => response.text())
      .then(result => {
        this.showMessage(result, 'success');
        this.fetchFileList();
        this.selectedFile = null;
      })
      .catch(err => this.showMessage('上传失败', 'error'));
    },
    downloadFile(fileName) {
      window.open(`${this.apiUrl}/download/${fileName}`);
    },
    deleteFile(fileName) {
      if (!confirm(`确定要删除文件 ${fileName} 吗？`)) return;

      fetch(`${this.apiUrl}/delete/${fileName}`, {
        method: 'DELETE'
      })
      .then(response => response.text())
      .then(result => {
        this.showMessage(result, 'success');
        this.fetchFileList();
      })
      .catch(err => this.showMessage('删除失败', 'error'));
    },
    showMessage(text, type) {
      this.message = text;
      this.messageType = type;
      setTimeout(() => {
        this.message = '';
      }, 3000);
    }
  }
};
</script>

<style scoped>
.oss-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.upload-section {
  margin-bottom: 20px;
  border: 1px dashed #ccc;
  padding: 15px;
  border-radius: 4px;
}

.file-list table {
  width: 100%;
  border-collapse: collapse;
}

.file-list th, .file-list td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.file-list th {
  background-color: #f4f4f4;
}

.delete-btn {
  background-color: #ff4d4f;
  color: white;
  margin-left: 10px;
}

.message {
  padding: 10px;
  margin: 10px 0;
  border-radius: 4px;
}

.success { background-color: #f6ffed; border: 1px solid #b7eb8f; color: #52c41a; }
.error { background-color: #fff2f0; border: 1px solid #ffccc7; color: #ff4d4f; }
.info { background-color: #e6f7ff; border: 1px solid #91d5ff; color: #1890ff; }

button {
  padding: 5px 15px;
  cursor: pointer;
}
</style>
