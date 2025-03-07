<template>
  <div>
    <!-- 头部区域 -->
    <div class="header">
      <div class="logo">
        <img src="@/assets/logo.png" alt="Logo" class="logo-img">
        <span class="logo-text">图书管理系统</span>
      </div>
      <div class="user-info">
        <el-dropdown>
          <span class="el-dropdown-link">
            {{ admin.username }}
            <i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item>
              <div class="logout" @click="logout">退出</div>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>

    <!-- 侧边栏和主体 -->
    <div class="main-container">
      <!-- 侧边栏导航 -->
      <div class="sidebar">
        <el-menu :default-active="$route.path" router class="el-menu-vertical" style="margin-bottom: 10px">
          <el-menu-item index="/">
            <i class="el-icon-s-home"></i>
            <span>首页</span>
          </el-menu-item>

          <el-submenu index="book">
            <template slot="title">
              <i class="el-icon-s-management"></i>
              <span>图书管理</span>
            </template>
            <el-menu-item index="/addBook">图书添加</el-menu-item>
            <el-menu-item index="/bookList">图书列表</el-menu-item>
          </el-submenu>

          <el-submenu index="borrow">
            <template slot="title">
              <i class="el-icon-collection"></i>
              <span>借书管理</span>
            </template>
            <el-menu-item index="/addBorrow">借书添加</el-menu-item>
            <el-menu-item index="/borrowList">借书列表</el-menu-item>
          </el-submenu>

          <el-submenu index="returnBook">
            <template slot="title">
              <i class="el-icon-s-claim"></i>
              <span>还书管理</span>
            </template>
            <el-menu-item index="/returnBookList">还书列表</el-menu-item>
          </el-submenu>

          <el-submenu index="user">
            <template slot="title">
              <i class="el-icon-user-solid"></i>
              <span>会员管理</span>
            </template>
            <el-menu-item index="/addUser">会员添加</el-menu-item>
            <el-menu-item index="/userList">会员列表</el-menu-item>
          </el-submenu>

          <el-submenu index="admin">
            <template slot="title">
              <i class="el-icon-s-platform"></i>
              <span>管理员管理</span>
            </template>
            <el-menu-item index="/addAdmin">管理员添加</el-menu-item>
            <el-menu-item index="/adminList">管理员列表</el-menu-item>
          </el-submenu>

          <el-submenu index="category">
            <template slot="title">
              <i class="el-icon-s-operation"></i>
              <span>图书分类管理</span>
            </template>
<!--        <el-menu-item index="/addCategory">图书分类添加</el-menu-item>-->
            <el-menu-item index="/categoryList">图书分类列表</el-menu-item>
          </el-submenu>
        </el-menu>
      </div>

      <!-- 主体数据 -->
      <div class="content">
        <router-view/>
      </div>
    </div>
  </div>
</template>

<script>
import Cookies from "js-cookie";

export default {
  name: "Layout",
  data() {
    return {
      admin: Cookies.get('admin') ? JSON.parse(Cookies.get('admin')) : {}
    }
  },
  methods: {
    logout() {
      // 清除浏览器用户数据
      Cookies.remove('admin')
      // 跳转登录界面
      this.$router.push('/login');
    }
  }
}
</script>

<style scoped>
.header {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #ffffff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
}

.logo-img {
  width: 40px;
  height: 40px;
  margin-right: 10px;
  border-radius: 50%;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.user-info {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  font-size: 16px;
  color: #333;
  cursor: pointer;
  transition: color 0.3s ease;
}

.el-dropdown-link:hover {
  color: #409eff;
}

.logout {
  width: 100%;
  text-align: center;
  color: #f56c6c;
  transition: color 0.3s ease;
}

.logout:hover {
  color: #ff7875;
}

.main-container {
  display: flex;
  min-height: calc(100vh - 60px);
}

.sidebar {
  width: 200px;
  background-color: #ffffff;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
}

.el-menu-vertical {
  border-right: none;
}

.content {
  flex: 1;
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}

.el-menu-item, .el-submenu__title {
  font-size: 16px;
  color: #333;
  transition: all 0.3s ease;
}

.el-menu-item:hover, .el-submenu__title:hover {
  background-color: #ecf5ff;
  border-radius: 4px;
}

.el-menu-item.is-active {
  background-color: #ecf5ff;
  color: #409eff;
  border-radius: 4px;
}

.el-submenu__title {
  font-weight: 500;
}

.el-menu-item i, .el-submenu__title i {
  margin-right: 10px;
}
</style>