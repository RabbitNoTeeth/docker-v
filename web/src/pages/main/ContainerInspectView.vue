<template>
  <q-dialog v-model="show" persistent>
    <q-card style="width: 900px; max-width: 80vw;">
      <q-card-section class="row items-center q-pb-none">
        <div class="text-h6">container inspect</div>
        <q-space/>
        <q-btn icon="close" flat round dense v-close-popup @click="close"/>
      </q-card-section>
      <q-card-section class="row items-center">
        <pre style="overflow: auto; background-color: #e0e0e0;width: 100%;height: 600px;">{{ JSON.stringify(JSON.parse(inspectInfo), null, 2) }}</pre>
      </q-card-section>
      <q-inner-loading :showing="loading">
        <div class="row">
          <q-spinner-gears size="50px" color="primary" />
          <span style="margin-left: 15px;font-size: 26px;color: #1976d2">loading...</span>
        </div>
      </q-inner-loading>
    </q-card>
  </q-dialog>
</template>

<script>

export default {
  name: "ContainerInspectView",
  data() {
    return {
      show: true,
      loading: false,
      inspectInfo: ''
    }
  },
  props: {
    data: {
      type: Object
    }
  },
  inject: ['sessionId'],
  mounted() {
    this.inspect();
  },
  methods: {
    close() {
      this.$emit('close')
    },
    inspect() {
      const app = this;
      app.loading = true;
      const url = '/api/container/inspect';
      app.$axios.post(url, {
        sessionId: app.sessionId,
        containerId: app.data.CONTAINER_ID
      })
        .then(res => {
          app.loading = false;
          if (res.data.success) {
            app.inspectInfo = res.data.data;
          } else {
            app.$q.notify({
              type: 'warning',
              position: 'center',
              multiLine: true,
              closeBtn: true,
              timeout: 30000,
              message: res.data.message
            });
          }
        })
        .catch(e => {
          app.loading = false;
          app.$q.notify({
            type: 'negative',
            position: 'top',
            message: 'could not inspect the container. ' + e
          });
        })
    }
  }
}
</script>

<style scoped>

</style>
