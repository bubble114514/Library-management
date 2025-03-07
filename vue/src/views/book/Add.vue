<template>
  <div style="width: 80%">
    <h2 style="margin-bottom: 30px">新增图书</h2>
    <el-form :model="form" status-icon :rules="rules" ref="ruleForm" style="margin-left: 50px;width: 80%"
             label-width="100px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入图书名称"></el-input>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input type="textarea" style="width:400px" v-model="form.description" placeholder="请输入描述"></el-input>
      </el-form-item>
      <el-form-item label="出版日期" prop="publishDate" width="100px">
        <el-date-picker
            style="width: 90%"
            v-model="form.publishDate"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="作者" prop="author">
        <el-input v-model="form.author" placeholder="请输入作者"></el-input>
      </el-form-item>
      <el-form-item label="出版社" prop="publisher">
        <el-input v-model="form.publisher" placeholder="请输入出版社"></el-input>
      </el-form-item>
      <el-form-item label="分类" prop="category">
        <el-cascader
            style="width: 220px"
            :props="{value:'name',label:'name'}"
            v-model="form.categories"
            :options="categories"
            placeholder="请选择分类"
            clearable
        ></el-cascader>
      </el-form-item>
      <el-form-item label="标准码" prop="bookNo">
        <el-input v-model="form.bookNo" placeholder="请输入标准码"></el-input>
      </el-form-item>

      <el-form :model="form" status-icon inline="true" :rules="rules" ref="ruleForm"
               style="margin-left: 20px;width: 80%">
        <el-form-item label="借书积分" prop="score">
          <el-input-number v-model="form.score" controls-position="right" :min="10" :max="999"
                           style="margin-right: 20px"
                           label="请输入所需积分"></el-input-number>
        </el-form-item>
        <el-form-item label="数量" prop="nums">
          <el-input-number v-model="form.nums" controls-position="right" :min="0" :max="99"
                           label="请输入库存数量"></el-input-number>
        </el-form-item>
      </el-form>


      <el-form-item label="封面" prop="cover">
        <el-upload
            class="avatar-uploader"
            :action="'http://localhost:9090/api/book/file/upload?token=' + this.admin.token"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
        >
          <img v-if="form.cover" :src="form.cover" class="avatar">
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
      </el-form-item>
    </el-form>
    <div style="text-align: center;margin-top: 40px">
      <el-button type="primary" @click="save" size="medium">提交</el-button>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import Cookies from "js-cookie";

export default {
  name: 'AddBook',
  data() {
    const checkNums = (rule, value, callback) => {
      value = parseInt(value)
      if (!Number.isInteger(value) || value < 0 || value > 1000) {
        return callback(new Error('请输入大于等于0小于1000的整数'));
      }
      callback();
    };
    return {
      admin: Cookies.get('admin') ? JSON.parse(Cookies.get('admin')) : {},
      categories: [],
      form: {score: 10, cover: '', nums: 1},
      rules: {
        name: [
          {required: true, message: '请输入图书名称', trigger: 'blur'},
        ],
        score: [
          {required: true, message: '请输入积分', trigger: 'blur'},
          {validator: checkNums, trigger: 'blur'},
        ],
        bookNo: [
          {required: true, message: '请输入标准码', trigger: 'blur'},
        ],
        nums: [
          {required: true, message: '请输入数量', trigger: 'blur'},
          {validator: checkNums, trigger: 'blur'},
        ],
      },
    }
  },
  created() {
    request.get('/category/tree').then((res) => {
      this.categories = res.data
    })
  },
  methods: {
    handleCoverSuccess(res) {
      if (res.code === '200') {
        console.log(res.data)
        this.form.cover = res.data
      } else {
        this.$message.error('上传失败，请重试');
      }
    },
    beforeCoverUpload(file) {
      // 检查 file 是否存在以及是否有 type 和 size 属性
      if (!file || !file.type || !file.size) {
        this.$message.error('上传的文件无效，请选择一个有效的文件');
        return false;
      }

      const validTypes = ['image/jpeg', 'image/png', 'application/pdf'];
      const isValidType = validTypes.includes(file.type);
      const isLt5M = file.size / 1024 / 1024 <= 5;

      if (!isValidType) {
        this.$message.error('上传封面只能是 JPG/PNG/PDF 格式!');
      }
      if (!isLt5M) {
        this.$message.error('上传封面图片大小不能超过 5MB!');
      }

      return isValidType && isLt5M;
    },

    save() {
      this.$refs['ruleForm'].validate((valid) => {
        if (valid) {
          request.post('book/save', this.form).then((res) => {
            if (res.code === '200') {
              // 提交表单数据
              this.$notify.success('新增成功');
              this.$refs['ruleForm'].resetFields(); // 重置表单
            } else {
              this.$notify.error(res.msg);
            }
          });
        }
      });
    },

  }
}
</script>
<style>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>

