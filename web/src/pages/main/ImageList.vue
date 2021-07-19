<template>
  <div style="width: 100%;">
    <q-table
      :data="data"
      :columns="columns"
      row-key="field"
      hide-bottom
      :pagination="{ rowsPerPage: 0 }"
    >
      <template v-slot:top>
        <div>
          <q-btn
            color="teal"
            size="sm"
            label="new"
            style="margin-right: 5px"
            @click="addClick"
          />
        </div>
        <div style="position: absolute;right: 15px">
          <el-input
            size="mini"
            placeholder="REPOSITORY"
            clearable
            style="width: 150px; margin-right: 5px"
            v-model="searchParams.REPOSITORY">
          </el-input>
          <el-input
            size="mini"
            placeholder="TAG"
            clearable
            style="width: 150px; margin-right: 5px"
            v-model="searchParams.TAG">
          </el-input>
          <el-input
            size="mini"
            placeholder="IMAGE_ID"
            clearable
            style="width: 150px; margin-right: 5px"
            v-model="searchParams.IMAGE_ID">
          </el-input>
          <q-btn
            color="primary"
            size="sm"
            label="search"
            style="margin-right: 5px"
            @click="refreshClick"
          />
          <q-btn
            color="primary"
            size="sm"
            outline
            label="reset"
            @click="resetClick"
          />
        </div>
      </template>
      <template v-slot:body-cell-CONTAINERS_TOTAL="props">
        <q-td :props="props">
          <span>{{props.row.CONTAINERS_UP}}/{{props.row.CONTAINERS_TOTAL}}</span>
          <q-tooltip>
            <span>up: {{props.row.CONTAINERS_UP}},&nbsp;</span>
            <span>exited: {{props.row.CONTAINERS_EXITED}},&nbsp;</span>
            <span>total: {{props.row.CONTAINERS_TOTAL}}</span>
          </q-tooltip>
        </q-td>
      </template>
      <template v-slot:body-cell-OPERATIONS="props">
        <q-td :props="props">
          <q-btn
            color="positive"
            size="xs"
            label="run"
            style="margin-right: 5px"
            @click="onRunClick(props.row)"
          />
          <q-btn
            v-if="props.row.CONTAINERS_TOTAL === 0"
            color="negative"
            size="xs"
            label="remove"
            style="margin-right: 5px"
            @click="onRemoveClick(props.row)"
          />
        </q-td>
      </template>
    </q-table>
    <image-add-form v-if="showAddForm" @close="onAddFormClose" @success="onAddFormSuccess"></image-add-form>
    <image-remove-confirm v-if="showRemoveConfirm" :data="curImage" @close="onRemoveConfirmClose" @success="onRemoveConfirmSuccess"></image-remove-confirm>
    <container-run-form v-if="showRunForm" :image="curImage" @close="onRunFormClose" @success="onRunFormSuccess"></container-run-form>
  </div>
</template>

<script>

import ImageAddForm from "pages/main/ImageAddForm";
import ImageRemoveConfirm from "pages/main/ImageRemoveConfirm";
import ContainerRunForm from "pages/main/ContainerRunForm";

export default {
  name: "ImageList",
  components: {ContainerRunForm, ImageRemoveConfirm, ImageAddForm},
  data() {
    return {
      data: [],
      columns: [
        {name: 'index', field: 'index', label: '#', align: 'left'},
        {name: 'REPOSITORY', field: 'REPOSITORY', label: 'REPOSITORY', align: 'left'},
        {name: 'TAG', field: 'TAG', label: 'TAG', align: 'left'},
        {name: 'IMAGE_ID', field: 'IMAGE_ID', label: 'IMAGE_ID', align: 'left'},
        {name: 'CREATED', field: 'CREATED', label: 'CREATED', align: 'left'},
        {name: 'SIZE', field: 'SIZE', label: 'SIZE', align: 'left'},
        {name: 'CONTAINERS_TOTAL', field: 'CONTAINERS_TOTAL', label: 'CONTAINERS', align: 'left', format: (val, row) => row.CONTAINERS_UP + '/' + val},
        {name: 'OPERATIONS', field: 'OPERATIONS', label: 'OPERATIONS', align: 'left'}
      ],
      searchParams: {},
      showAddForm: false,
      showRemoveConfirm: false,
      showRunForm: false,
      curImage: null
    }
  },
  inject: ['sessionId'],
  mounted() {
    const app = this;
    app.queryList();
    if (app.refreshInterval) {
      clearInterval(app.refreshInterval);
    }
    app.refreshInterval = setInterval(() => app.queryList(), 10000);
  },
  destroyed() {
    const app = this;
    if (app.refreshInterval) {
      clearInterval(app.refreshInterval);
    }
  },
  methods: {
    queryList() {
      const app = this;
      app.$axios.get('/api/image/list', {
        params: {
          sessionId: app.sessionId,
          ...app.searchParams
        }
      })
        .then(res => {
          if (res.data.success) {
            app.data = res.data.data;
            for (let i = 0; i < app.data.length; i++) {
              app.data[i].index = i + 1;
            }
          } else {
            app.$q.notify({
              type: 'warning',
              position: 'center',
              multiLine: true,
              closeBtn: true,
              timeout: 30000,
              message: 'could not load images: ' + res.data.message
            });
          }
        })
        .catch(e => {
          app.$q.notify({
            type: 'negative',
            position: 'top',
            message: 'could not load images. ' + e
          });
        })
    },
    refreshClick() {
      this.queryList();
    },
    resetClick() {
      this.searchParams = {};
      this.queryList();
    },
    addClick() {
      this.showAddForm = true;
    },
    onAddFormClose() {
      this.showAddForm = false;
    },
    onAddFormSuccess() {
      this.onAddFormClose();
      this.queryList();
    },
    onRemoveClick(image) {
      this.curImage = image;
      this.showRemoveConfirm = true;
    },
    onRemoveConfirmClose() {
      this.curImage = null;
      this.showRemoveConfirm = false;
    },
    onRemoveConfirmSuccess() {
      this.onRemoveConfirmClose();
      this.queryList();
    },
    onRunClick(image) {
      this.curImage = image;
      this.showRunForm = true;
    },
    onRunFormClose() {
      this.curImage = null;
      this.showRunForm = false;
    },
    onRunFormSuccess() {
      this.onRunFormClose();
      this.queryList();
    }
  }
}
</script>

<style scoped>

</style>
