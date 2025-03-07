<template>
  <div style="width: 80%">
    <h2 style="margin-bottom: 30px">编辑管理员</h2>
    <el-form :model="form" status-icon :rules="rules" style="width: 80%" label-width="120px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item label="联系方式" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入联系方式"></el-input>
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model.number="form.email" placeholder="请输入邮箱"></el-input>
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
  name: 'Edit',
  data() {
    const checkPhone = (rule, value, callback) => {
      if(!/^[1][3-9][0-9]{9}$/.test(value)){
        callback(new Error('请正确输入手机号'));
      }
      callback();
    };
    return {
      form: {},
      rules: {
        // 表单验证规则
        username: [
          {required: true, message: '请输入用户名', trigger: 'blur'},
          {min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur'}
        ],
        phone: [
          {validator: checkPhone, trigger: 'blur'}
        ],
      },
    }
  },
  created() {
    request.get('/admin/' + this.$route.query.id).then((res) => {
      this.form = res.data
    })
  },
  methods: {
    save() {
      request.put('/admin/update', this.form).then((res) => {
        if (res.code === '200') {
          this.$notify.success('更新成功')
          this.$router.push('/adminList')
        } else {
           this.$notify.error(res.msg);
        }
      });
    }
  }
}
</script>
