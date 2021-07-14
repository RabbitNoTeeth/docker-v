<template>
  <q-layout view="hHh lpr fFf">

    <q-header elevated class="bg-primary text-white" height-hint="120">
      <q-tabs
        v-model="tab"
        align="left"
        inline-label
        class="bg-primary text-white shadow-2"
      >
        <template v-for="(item, index) in tabs">
          <q-tab :key="index" :name="item.id" icon="fas fa-terminal" :label="item.name"/>
        </template>
      </q-tabs>
    </q-header>

    <q-page-container style="width: 100%;height: 100%;position: fixed">
      <q-tab-panels v-model="tab" animated>
        <template v-for="(item, index) in tabs">
          <q-tab-panel :key="index" :name="item.id" style="width: 100%;height: 100%">
            <session :id="item.id"></session>
          </q-tab-panel>
        </template>
      </q-tab-panels>
    </q-page-container>

  </q-layout>

</template>

<script>

import Session from "pages/Session";
export default {
  name: 'MainLayout',
  components: {Session},
  data() {
    return {
      tab: null,
      tabs: []
    }
  },
  mounted() {
    const app = this;
    app.queryList();
  },
  methods: {
    queryList() {
      const app = this;
      app.$axios.get('/api/session/list')
        .then(res => {
          if (res.data.success) {
            app.tabs = res.data.data;
            if (app.tabs.length === 0) {
              app.tabs.push({
                id: 'new',
                name: 'create session',
                host: ''
              });
              app.tab = 'new';
            }
          } else {
            app.$q.notify({
              type: 'warning',
              position: 'top',
              message: 'failed to load session list: ' + res.data.message
            });
          }
        })
        .catch(e => {
          app.$q.notify({
            type: 'negative',
            position: 'top',
            message: 'failed to load session list. ' + e
          });
        })
    }
  }
}
</script>
