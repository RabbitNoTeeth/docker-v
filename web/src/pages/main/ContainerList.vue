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
        <div>
        </div>
      </template>
      <template v-slot:body-cell-operations="props">
        <q-td :props="props">

        </q-td>
      </template>
    </q-table>
  </div>
</template>

<script>

export default {
  name: "ContainerList",
  components: {},
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
      ]
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
          sessionId: app.sessionId
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
    addClick() {
      // todo
    }
  }
}
</script>

<style scoped>

</style>
