<template>
  <div class="center" style="position: relative">
    <el-card class="cover" v-if="loginAdmin.id">
      <slide-verify ref="slideblock" @success="onSuccess" @again="onAgain" @fulfilled="onFulfilled" @fail="onFail" @refresh="onRefresh" :slider-text="text" :imgs="imgs" :accuracy="accuracy"></slide-verify>
      <div>{{msg}}</div>
    </el-card>
    <div class="logon">
      <div class="overlaylong" :class="{ 'overlaylongleft': disfiex === 1, 'overlaylongright': disfiex === 0 }">
        <el-form ref="ruleForm" :model="admin" :rules="rules" v-if="disfiex === 0">
          <div class="overlaylong-Signin">
            <h2 class="overlaylongH2">Welcome！</h2>
            <el-form-item prop="username">
              <input type="text" placeholder="用户名" v-model="admin.username">
            </el-form-item>
            <el-form-item prop="password">
              <input type="password" placeholder="密码" v-model="admin.password">
            </el-form-item>
            <el-button type="primary" @click="login" style="width: 180px; height: 40px; border-radius: 50px;
              font-size: 15px; color: #fff; text-align: center; line-height: 20px; margin-top: 30px;">登录
            </el-button>
          </div>
        </el-form>
        <el-form ref="ruleForm" :model="admin" :rules="rules" v-if="disfiex === 1">
          <div class="overlaylong-Signup">
            <h2 class="overlaylongH2">注册账户</h2>
            <el-form-item prop="username">
              <input type="text" placeholder="用户名" v-model="admin.username">
            </el-form-item>
            <el-form-item prop="password">
              <input type="password" placeholder="密码" v-model="admin.password">
            </el-form-item>
            <el-form-item prop="phone">
              <input type="text" placeholder="手机号" v-model="admin.phone">
            </el-form-item>
            <el-form-item prop="email">
              <input type="text" placeholder="邮箱" v-model="admin.email">
            </el-form-item>
            <el-button type="primary" @click="signup" style="width: 180px; height: 40px; border-radius: 50px;
              font-size: 15px; color: #fff; text-align: center; line-height: 20px;">注册
            </el-button>
          </div>
        </el-form>
      </div>
      <div :class="{ 'overlaytitle': true, 'overlaytitleright': disfiex === 1, 'overlaytitleleft': disfiex === 0 }">
        <div class="overlaytitle-Signin" v-if="disfiex === 0">
          <h2 class="overlaytitleH2">Hello,Friend！</h2>
          <p class="overlaytitleP">
            注册用户并开始与我们的旅程
          </p>
          <div class="buttongohs" @click="Signin">注册</div>
        </div>
        <div class="overlaytitle-Signup" v-if="disfiex === 1">
          <h2 class="overlaytitleH2">Welcome！</h2>
          <p class="overlaytitleP">为了保持与我们的联系，请使用您的个人信息登录</p>
          <div class="buttongohs" @click="Signup">登录</div>
        </div>
      </div>
    </div>
  </div>
</template>



<script>
import request from "@/utils/request";
import Cookies from 'js-cookie'

import img1 from '@/assets/img1.png';
import img2 from '@/assets/img2.png';
import img3 from '@/assets/img3.png';
import img4 from '@/assets/img4.png';
import img5 from '@/assets/img5.png';

export default {
  name: "Login",
  data() {
    return {
      msg: '',
      text: '向右滑动→',
      imgs: [
        img1,
        img2,
        img3,
        img4,
        img5,
      ],
      accuracy: 5, // 滑块对齐精确度
      loginAdmin: {},
      admin: {
        username: '',
        password: '',
        phone: '',
        email: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 3, message: '密码长度至少为3个字符', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
        ]
      },
      overlaylong: 'overlay long',
      overlaytitle: 'overlay title',
      disfiex: 0
    }
  },

  methods: {
    login() {
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          request.post('/admin/login', this.admin).then(res => {
            if (res.code === '200') {
              this.loginAdmin = res.data //滑块组件出现
            } else {
              this.$notify.error(res.msg || "登录失败，请检查用户名和密码");
            }
          }).catch((error) => {
            console.error('请求出错:', error);
            this.$notify.error("请求出错，请稍后再试");
          });
        } else {
          this.$notify.error("表单验证失败，请检查输入");
        }
      });
    },
    signup() {
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          request.post('/admin/signup', this.admin).then(res => {
            if (res.code === '200') {
              this.$notify.success("注册成功!");
              this.$refs.ruleForm.resetFields();

            } else {
              this.$notify.error(res.msg || "未知错误");
            }
          }).catch((error) => {
            console.error('请求出错:', error);
            this.$notify.error("请求出错，请稍后再试");
          });
        } else {
          this.$notify.error("表单验证失败，请检查输入");
        }
      });
    },
    onSuccess() {//回调函数，滑块验证通过之后触发
      Cookies.set('admin', JSON.stringify(this.loginAdmin))
      this.$notify.success("登录成功!");
      this.$router.push('/');
      //先存数据数据，再跳转
      //如果先跳转就会先触发路由守卫，此时cookie没有数据就会导致重复跳转登录页而报错
      if (res.data != null) {
        Cookies.set('admin', JSON.stringify(res.data))
      }


    },
    onFail() {//验证失败
      this.$notify.error("验证失败!");
    },
    onRefresh() {//滑块刷新
      console.log("refresh");
    },

    toggleOverlay(isSignIn) {
      this.overlaylong = isSignIn ? "overallocation" : "overregularization";
      this.overlaytitle = isSignIn ? "overregularization" : "overexploitation";
      this.$nextTick(() => {
        this.disfiex = isSignIn ? 1 : 0;
      });
    },
    Signin() {
      this.toggleOverlay(true);
    },
    Signup() {
      this.toggleOverlay(false);
    }
  }

}
</script>

<style>
.center {
  width: 100vw;
  height: 100vh;
  background-image: url('https://ts1.cn.mm.bing.net/th/id/R-C.5c8f29ad51f34d3b0adc3afdb291df93?rik=%2bhG3H5kH6s4iDA&riu=http%3a%2f%2fpic.zsucai.com%2ffiles%2f2013%2f0716%2fpgjlg3.jpg&ehk=6C%2bwmXLxbeNFL6yxRnn1FEwx%2fLA50Y%2f3aisA53qH%2foQ%3d&risl=&pid=ImgRaw&r=0');
  background-size: cover;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

h1 {
  font-size: 30px;
  color: black;
}

.logon {
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 14px 28px rgba(0, 0, 0, 0.25), 0 10px 10px rgba(0, 0, 0, 0.22);
  width: 768px;
  max-width: 100%;
  min-height: 480px;
  margin-top: 20px;
  display: flex;
  background: -webkit-linear-gradient(#4284db, #29eac4);
}

.overlaylong {
  border-radius: 10px 0 0 10px;
  width: 50%;
  height: 100%;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
}

.overlaylongleft {
  border-radius: 0px 10px 10px 0px;
  transform: translateX(100%);
  transition: transform 0.6s ease-in-out;
}

.overlaylongright {
  border-radius: 10px 0 0 10px;
  transform: translateX(0%);
  transition: transform 0.6s ease-in-out;
}

.overlaytitle {
  border-radius: 0px 10px 10px 0px;
  width: 50%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0);
  display: flex;
  align-items: center;
  justify-content: center;
}

.overlaytitleH2 {
  font-size: 30px;
  color: #fff;
  margin-top: 20px;
}

.overlaytitleP {
  font-size: 15px;
  color: #fff;
  margin-top: 20px;
}

.overlaytitleleft {
  transform: translateX(0%);
  transition: transform 0.6s ease-in-out;
}

.overlaytitleright {
  transform: translateX(-100%);
  transition: transform 0.6s ease-in-out;
}

.overlaytitle-Signin,
.overlaytitle-Signup {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

.buttongohs {
  width: 180px;
  height: 40px;
  border-radius: 50px;
  border: 1px solid #fff;
  color: #fff;
  font-size: 15px;
  text-align: center;
  line-height: 40px;
  margin-top: 40px;
}

.overlaylongH2 {
  font-size: 25px;
  color: black;
}

.overlaylong-Signin,
.overlaylong-Signup {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

input {
  background-color: #eee;
  border: none;
  padding: 12px 15px;
  margin: 10px 0;
  width: 240px;
}

h3 {
  font-size: 10px;
  margin-top: 10px;
  cursor: pointer;
}

.inupbutton {
  background-color: #29eac4;
  border: none;
  width: 180px;
  height: 40px;
  border-radius: 50px;
  font-size: 15px;
  color: #fff;
  text-align: center;
  line-height: 40px;
  margin-top: 30px;
}
.cover {
  width: fit-content;
  background-color: white;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 99999;
}
</style>
