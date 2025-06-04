<template>
  <div>
    <el-row>
      <el-col :span="20">
        <el-input v-model="keyWordInput"></el-input>
      </el-col>
      <el-col :span="4">
        <el-button @click="searchTxtClick" :type="'primary'">搜索</el-button>
      </el-col>
    </el-row>
    <div style="padding: 5px">
      <el-card style="margin: 3px" v-for="item in downloadList">
        <p>书名: {{ item.bookName }}</p>
        <p>作者: {{ item.author }}</p>
        <p>最后更新时间: {{ item.lastUpdateTime }}</p>
        <p>{{ item.url }}</p>
        <p>
          <el-button type="primary" @click="downloadBook(item)">下载</el-button>
        </p>
      </el-card>
    </div>
  </div>
</template>
<script setup lang="ts">
import {ref} from "vue";
import req from "@/request.ts";

const keyWordInput = ref("")
const downloadList = ref<Array<any>>([])
const searchTxtClick = async () => {
  const resp = await req.get('/novel/search', {
    params: {
      'q': keyWordInput.value
    }
  })
  downloadList.value = resp.data
}
const downloadBook = async (searchResult: any) => {
  const res = await req.post('/novel/download', searchResult, {
    responseType: 'blob'
  })
  const blob = res.data;
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = 'workbook.txt';  // 可以根据需要修改文件名
  link.click();
  URL.revokeObjectURL(link.href);
}
</script>
<style scoped>
p {
  word-wrap: break-word;
  white-space: normal;
}
</style>
