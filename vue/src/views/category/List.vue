<template>
  <div class="category-container">
    <!-- 搜索和操作区域 -->
    <div class="search-operation">
      <el-input
          style="width: 240px; margin-right: 10px"
          placeholder="请输入分类名称"
          v-model="params.name"
          clearable
      ></el-input>
      <el-button type="primary" @click="load">
        <i class="el-icon-search"></i> 搜索
      </el-button>
      <el-button type="warning" @click="reset">
        <i class="el-icon-refresh"></i> 重置
      </el-button>
      <el-button type="success" @click="addMainDialogVisible = true">
        <i class="el-icon-circle-plus-outline"></i> 新建
      </el-button>
    </div>

    <!-- 树形控件显示分类 -->
    <div class="tree-container">
      <el-tree
          v-if="dataLoaded"
          :data="treeData"
          node-key="id"
          default-expand-all
          :props="defaultProps"
          :expand-on-click-node="false"
      >
        <!-- 自定义树节点的内容 -->
        <template v-slot="{ node, data }">
          <span class="custom-tree-node">
            <span>{{ node.label }}</span>
            <span>
              <el-button type="success" size="mini" @click="handleAdd(data)">添加子分类</el-button>
              <el-button type="primary" size="mini" @click="handleEdit(data)">编辑</el-button>
              <el-popconfirm
                  title="确定删除吗？"
                  @confirm="del(data.id)"
                  style="margin-left: 5px"
              >
                <el-button type="danger" size="mini" slot="reference">删除</el-button>
              </el-popconfirm>
            </span>
          </span>
        </template>
      </el-tree>
      <div v-else class="loading-text">加载中...</div>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
          background
          :current-page="params.pageNum"
          :page-size="params.pageSize"
          layout="prev, pager, next"
          @current-change="handleCurrentChange"
          :total="total"
      ></el-pagination>
    </div>

    <!-- 添加大分类的弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="addMainDialogVisible" width="35%">
      <el-form :model="form" label-width="100px" ref="addMainForm" :rules="rules">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="分类备注">
          <el-input v-model="form.remark"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="addMainCategory">确 定</el-button>
        <el-button @click="addMainDialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 添加子分类的弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="addDialogVisible" width="35%">
      <el-form :model="form" label-width="100px" ref="addForm" :rules="rules">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="分类备注">
          <el-input v-model="form.remark"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="save">确 定</el-button>
        <el-button @click="addDialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 编辑分类的弹窗 -->
    <el-dialog :title="dialogTitle" :visible.sync="editDialogVisible" width="35%">
      <el-form :model="form" label-width="100px" ref="editForm" :rules="rules">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="分类备注">
          <el-input v-model="form.remark"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="update">确 定</el-button>
        <el-button @click="editDialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request';
import Cookies from "js-cookie";

export default {
  name: 'CategoryList',
  data() {
    return {
      admin: Cookies.get('admin') ? JSON.parse(Cookies.get('admin')) : {},
      addMainDialogVisible: false,
      addDialogVisible: false,
      editDialogVisible: false,
      dialogTitle: '添加分类',
      form: {},
      pid: '',
      treeData: [],
      total: 0,
      params: {
        name: '',
        pageNum: 1,
        pageSize: 25,
      },
      dataLoaded: false,
      defaultProps: {
        children: 'children',
        label: 'name',
      },
      rules: {
        name: [
          {required: true, message: '请输入分类名称', trigger: 'blur'},
        ],
      },
    };
  },
  created() {
    this.load();
  },
  methods: {
    load() {
      request.get('/category/page', {
        params: this.params,
      }).then((res) => {
        if (res && res.code === '200') {
          this.treeData = res.data.list;
          this.total = res.data.total;
          this.dataLoaded = true;
        }
      }).catch((error) => {
        console.error('加载数据出错：', error);
        this.dataLoaded = false;
      });
    },
    handleCurrentChange(pageNum) {
      this.params.pageNum = pageNum;
      this.load();
    },
    reset() {
      this.params = {
        name: '',
        pageNum: 1,
        pageSize: 15,
      };
      this.load();
    },
    del(id) {
      request.delete('/category/delete/' + id).then((res) => {
        if (res.code === '200') {
          this.$notify.success('删除成功');
          this.load();
        } else {
          this.$notify.error(res.msg);
        }
      }).catch((error) => {
        this.$notify.error('删除失败，请重试');
        console.error('Delete failed:', error);
      });
    },
    addMainCategory() {
      this.$refs['addMainForm'].validate((valid) => {
        if (valid) {
          request.post('category/save', this.form).then((res) => {
            if (res.code === '200') {
              this.$notify.success('新增成功');
              this.$refs['addMainForm'].resetFields();
              this.addMainDialogVisible = false;
              this.load();
            } else {
              this.$notify.error(res.msg);
            }
          });
        }
      });
    },
    save() {
      this.$refs['addForm'].validate((valid) => {
        if (valid) {
          if (this.dialogTitle === '添加子分类') {
            this.form.pid = this.pid;
          }
          request.post('/category/save', this.form).then((res) => {
            if (res.code === '200') {
              this.$notify.success(this.dialogTitle === '添加子分类' ? '新增分类成功' : '添加分类成功');
              this.$refs['addForm'].resetFields();
              this.addDialogVisible = false;
              this.load();
            } else {
              this.$notify.error(res.msg);
            }
          });
        }
      });
    },
    handleAdd(row) {
      this.pid = row.id;
      this.dialogTitle = '添加子分类';
      this.form = {};
      this.addDialogVisible = true;
    },
    handleEdit(row) {
      this.form = {...row};
      this.dialogTitle = '编辑分类';
      this.editDialogVisible = true;
    },
    update() {
      this.$refs['editForm'].validate((valid) => {
        if (valid) {
          request.put('/category/update', this.form).then((res) => {
            if (res.code === '200') {
              this.$notify.success('更新成功');
              this.editDialogVisible = false;
              this.load();
            } else {
              this.$notify.error(res.msg);
            }
          });
        }
      });
    },
  },
};
</script>

<style scoped>
.category-container {
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.search-operation {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.tree-container {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.el-tree >>> .el-tree-node {
  margin: 5px 0;
}

.el-tree >>> .el-tree-node__children {
  padding-left: 5px;
}

.pagination-container {
  margin-top: 20px;
  text-align: center;
}

.loading-text {
  text-align: center;
  color: #909399;
  font-size: 14px;
}
</style>