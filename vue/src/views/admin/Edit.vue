<template>
  <div style="width: 80%">
    <h2 style="margin-bottom: 30px">编辑用户</h2>
    <el-form :model="form" status-icon :rules="rules" style="width: 80%" label-width="120px">
      <el-form-item label="卡号" prop="username">
        <el-input v-model="form.username"  disabled></el-input>
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入姓名"></el-input>
      </el-form-item>
      <el-form-item label="年龄" prop="age">
        <el-input v-model.number="form.age" placeholder="请输入年龄" type="number"></el-input>
      </el-form-item>
      <el-form-item label="性别" prop="sex">
        <el-input v-model="form.sex" placeholder="请输入性别"></el-input>
      </el-form-item>
      <el-form-item label="联系方式" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入联系方式"></el-input>
      </el-form-item>
      <el-form-item label="地址" prop="address">
        <el-input v-model="form.address" placeholder="请输入地址"></el-input>
      </el-form-item>
    </el-form>
    <div style="text-align: center;margin-top: 40px">
      <el-button type="primary" @click="save" size="medium">提交</el-button>
    </div>
  </div>
</template>

<script>
import request from "@/utils/request";
import axios from 'axios';
export default {
  name: 'AddUser',
  data() {
    return {
      form: {},
      rules: {
        // 表单验证规则
      },
    }
  },
  created() {
    request.get('/user/' + this.$route.query.id).then((res) => {
      this.form = res.data
    })
  },
  methods: {
    save() {
      request.put('/user/update', this.form).then((res) => {
        if (res.code === '200') {
          this.$notify.success('更新成功')
          this.$router.push('/user')
        } else {
          this.$notify.error(res.msg);
        }
      });
    }
  }
}
</script>
