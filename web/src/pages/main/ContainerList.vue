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
            placeholder="NAMES"
            clearable
            style="width: 150px; margin-right: 5px"
            v-model="searchParams.NAMES">
          </el-input>
          <el-input
            size="mini"
            placeholder="CONTAINER_ID"
            clearable
            style="width: 150px; margin-right: 5px"
            v-model="searchParams.CONTAINER_ID">
          </el-input>
          <el-input
            size="mini"
            placeholder="IMAGE"
            clearable
            style="width: 150px; margin-right: 5px"
            v-model="searchParams.IMAGE">
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
      <template v-slot:body-cell-PORTS="props">
        <q-td :props="props">
          <div v-if="props.row.PORTS && (props.row.PORTS + '').length > 30">
            <span>{{ (props.row.PORTS + '').substring(0, 30) }}&nbsp;...</span>
            <q-tooltip>
              <p :key="index" v-for="(item, index) in props.row.PORTS.split(',')">{{item}}</p>
            </q-tooltip>
          </div>
          <div v-else>{{ props.row.PORTS }}</div>
        </q-td>
      </template>
      <template v-slot:body-cell-OPERATIONS="props">
        <q-td :props="props">
          <q-btn
            color="primary"
            size="xs"
            label="inspect"
            style="margin-right: 5px"
            @click="onInspectClick(props.row)"
          />
          <q-btn
            v-if="props.row.STATUS.indexOf('Exited') > -1 || props.row.STATUS.indexOf('Created') > -1"
            color="positive"
            size="xs"
            label="start"
            style="margin-right: 5px"
            @click="onStartClick(props.row)"
          />
          <q-btn
            v-if="props.row.STATUS.indexOf('Up') > -1"
            color="warning"
            size="xs"
            label="stop"
            style="margin-right: 5px"
            @click="onStopClick(props.row)"
          />
          <q-btn
            v-if="props.row.STATUS.indexOf('Exited') > -1 || props.row.STATUS.indexOf('Created') > -1"
            color="negative"
            size="xs"
            label="remove"
            style="margin-right: 5px"
            @click="onRemoveClick(props.row)"
          />
        </q-td>
      </template>
    </q-table>
    <container-start-confirm v-if="showStartConfirm" :data="curContainer" @close="onStartConfirmClose"
                             @success="onStartConfirmSuccess"></container-start-confirm>
    <container-stop-confirm v-if="showStopConfirm" :data="curContainer" @close="onStopConfirmClose"
                            @success="onStopConfirmSuccess"></container-stop-confirm>
    <container-remove-confirm v-if="showRemoveConfirm" :data="curContainer" @close="onRemoveConfirmClose"
                              @success="onRemoveConfirmSuccess"></container-remove-confirm>
    <container-add-form v-if="showAddForm" @close="onAddFormClose" @success="onAddFormSuccess"></container-add-form>

    <container-inspect-view v-if="showInspectView" :data="curContainer" @close="onInspectViewClose"></container-inspect-view>
  </div>
</template>

<script>

import ContainerStartConfirm from "pages/main/ContainerStartConfirm";
import ContainerStopConfirm from "pages/main/ContainerStopConfirm";
import ContainerRemoveConfirm from "pages/main/ContainerRemoveConfirm";
import ContainerAddForm from "pages/main/ContainerAddForm";
import ContainerInspectView from "pages/main/ContainerInspectView";

export default {
  name: "ContainerList",
  components: {
    ContainerInspectView,
    ContainerAddForm, ContainerRemoveConfirm, ContainerStopConfirm, ContainerStartConfirm},
  data() {
    return {
      data: [],
      columns: [
        {name: 'index', field: 'index', label: '#', align: 'left'},
        {name: 'CONTAINER_ID', field: 'CONTAINER_ID', label: 'CONTAINER_ID', align: 'left'},
        {name: 'IMAGE', field: 'IMAGE', label: 'IMAGE', align: 'left'},
        {name: 'COMMAND', field: 'COMMAND', label: 'COMMAND', align: 'left'},
        {name: 'CREATED', field: 'CREATED', label: 'CREATED', align: 'left'},
        {name: 'STATUS', field: 'STATUS', label: 'STATUS', align: 'left'},
        {name: 'PORTS', field: 'PORTS', label: 'PORTS', align: 'left'},
        {name: 'NAMES', field: 'NAMES', label: 'NAMES', align: 'left'},
        {name: 'OPERATIONS', field: 'OPERATIONS', label: 'OPERATIONS', align: 'left'}
      ],
      showStartConfirm: false,
      showStopConfirm: false,
      showRemoveConfirm: false,
      curContainer: null,
      searchParams: {},
      showAddForm: false,
      showInspectView: false
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
      app.$axios.get('/api/container/list', {
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
              position: 'top',
              message: 'could not load containers: ' + res.data.message
            });
          }
        })
        .catch(e => {
          app.$q.notify({
            type: 'negative',
            position: 'top',
            message: 'could not load containers. ' + e
          });
        })
    },
    onStartClick(container) {
      this.curContainer = container;
      this.showStartConfirm = true;
    },
    onStartConfirmClose() {
      this.curContainer = null;
      this.showStartConfirm = false;
    },
    onStartConfirmSuccess() {
      this.onStartConfirmClose();
      this.queryList();
    },
    onStopClick(container) {
      this.curContainer = container;
      this.showStopConfirm = true;
    },
    onStopConfirmClose() {
      this.curContainer = null;
      this.showStopConfirm = false;
    },
    onStopConfirmSuccess() {
      this.onStopConfirmClose();
      this.queryList();
    },
    onRemoveClick(container) {
      this.curContainer = container;
      this.showRemoveConfirm = true;
    },
    onRemoveConfirmClose() {
      this.curContainer = null;
      this.showRemoveConfirm = false;
    },
    onRemoveConfirmSuccess() {
      this.onRemoveConfirmClose();
      this.queryList();
    },
    addClick() {
      this.showAddForm = true;
    },
    refreshClick() {
      this.queryList();
    },
    resetClick() {
      this.searchParams = {};
      this.queryList();
    },
    onAddFormClose() {
      this.showAddForm = false;
    },
    onAddFormSuccess() {
      this.onRunFormClose();
      this.queryList();
    },
    onInspectClick(container) {
      this.curContainer = container;
      this.showInspectView = true;
    },
    onInspectViewClose() {
      this.curContainer = null;
      this.showInspectView = false;
    }
  }
}
</script>

<style scoped>

</style>
