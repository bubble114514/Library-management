<template>
  <div style="width: 80%">
    <h2 style="margin-bottom: 30px">编辑图书信息</h2>
    <el-form :model="form" :inline="true" status-icon :rules="rules" ref="ruleForm" style="width: 100%" label-width="120px" >
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入图书名称"></el-input>
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input type="textarea" style="width:400px" v-model="form.description" placeholder="请输入描述"></el-input>
      </el-form-item>
      <el-form-item label="出版日期" prop="publishDate">
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
      <el-form-item label="借书积分" prop="score">
        <el-input v-model="form.score" placeholder="请输入积分"></el-input>
      </el-form-item>
      <el-form-item label="封面" prop="cover">
        <el-upload
            class="avatar-uploader"
            :action="'http://localhost:9090/api/book/file/upload?token=' + this.admin.token"
            :show-file-list="false"
            :on-success="handleCoverSuccess"
            :before-upload="beforeCoverUpload"
        >
          <img v-if="form.cover" :src="form.cover" class="avatar" >
          <i v-else class="el-icon-plus avatar-uploader-icon"></i>
        </el-upload>
      </el-form-item>
      <el-form-item label="数量" prop="nums">
        <el-input v-model="form.nums" placeholder="请输入数量"></el-input>
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
  name: 'EditBook',
  data() {
    const checkNums = (rule, value, callback) => {
      value=parseInt(value)
      if (!Number.isInteger(value) || value <0 || value > 1000) {
        return callback(new Error('请输入大于等于0小于1000的整数'));
      }
      callback();
    };
    return {
      admin: Cookies.get('admin') ? JSON.parse(Cookies.get('admin')) : {},
      categories: [],
      form: {},
      rules: {
        name: [
          {required: true, message: '请输入分类名称', trigger: 'blur'},
        ],
        score: [
          {required: true, message: '请输入借书积分', trigger: 'blur'},
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
    const id = this.$route.query.id
    request.get('/book/' + id).then((res) => {
      this.form = res.data
      if(this.form.category){
        this.form.categories = this.form.category.split('/')
        console.log(this.form.categories)
      }
    })

  },

  methods: {
    handleCoverSuccess(res) {
      if (res.code === '200'){
        console.log(res.data)
        this.form.cover = res.data
      }else {
        this.$message.error('上传失败，请重试');
      }
    },
    beforeCoverUpload(file) {
      const isJPG = file.type === 'image/jpeg';
      const isPNG = file.type === 'image/png';
      const isPDF = file.type === 'application/pdf';
      const isLt2M = file.size / 1024 / 1024 < 5;

      if (!isJPG||!isPDF||!isPNG) {
        this.$message.error('上传封面只能是 JPG/PDF/PNG 格式!');
      }
      if (!isLt2M) {
        this.$message.error('上传封面图片大小不能超过 5MB!');
      }
      return isJPG && isLt2M;
    },
    save() {
      request.put('/book/update', this.form).then((res) => {
        if (res.code === '200') {
          this.$notify.success('更新成功')
          this.$router.push('/bookList')
        } else {
           this.$notify.error(res.msg);
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