<template>
  <div style="width: 80%">
    <h2 style="margin-bottom: 30px">新增图书</h2>
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
      <el-form-item label="封面" prop="cover">
        <el-input v-model="form.cover" placeholder="请输入封面"></el-input>
      </el-form-item>
    </el-form>
    <div style="text-align: center;margin-top: 40px">
      <el-button type="primary" @click="save" size="medium">提交</el-button>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";

export default {
  name: 'AddBook',
  data() {
    return {
      categories: [],
      form: {},
      rules: {
        name: [
          {required: true, message: '请输入图书名称', trigger: 'blur'},
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
