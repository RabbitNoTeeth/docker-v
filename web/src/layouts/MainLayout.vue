<template>
  <q-layout view="hHh lpr fFf">

    <q-header elevated class="bg-primary text-white" height-hint="120">
      <q-tabs
        v-model="tab"
        align="left"
        inline-label
        no-caps
        class="bg-primary text-white shadow-2"
      >
        <template v-for="(item, index) in tabs">
          <q-tab :key="index" :name="item.id" icon="fas fa-terminal">
            <template v-slot:default>
              <span style="margin-left: 10px">{{ item.name }}</span>
              <q-icon v-if="item.id !== 'new'" name="fas fa-times" color="orange"
                      style="margin-left: 15px;font-size: 1.5em;" @click="onCloseClick(item)"/>
            </template>
          </q-tab>
        </template>
      </q-tabs>
    </q-header>

    <q-page-container style="width: 100%;height: 100%;position: fixed;">
      <q-tab-panels v-model="tab" animated style="width: 100%;height: 100%">
        <template v-for="(item, index) in tabs">
          <q-tab-panel :key="index" :name="item.id" style="width: 100%;height: 100%">
            <session :id="item.id" @add-session="onAddSession"></session>
          </q-tab-panel>
        </template>
      </q-tab-panels>
    </q-page-container>

    <session-close-confirm v-if="showSessionCloseConfirm" :session="curSession" @close="onSessionCloseConfirmClose"
                           @success="onSessionCloseConfirmSuccess"></session-close-confirm>
  </q-layout>

</template>

<script>

import Session from "pages/main/Session";
import SessionCloseConfirm from "pages/main/SessionCloseConfirm";

export default {
  name: 'MainLayout',
  components: {SessionCloseConfirm, Session},
  data() {
    return {
      tab: null,
      tabs: [],
      showSessionCloseConfirm: false,
      curSession: null
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
            app.tabs.push({
              id: 'new',
              name: 'new session',
              host: ''
            });
            app.tab = app.tabs[0].id;
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
    },
    onAddSession(session) {
      const app = this;
      const createsessiontab = app.tabs[app.tabs.length - 1];
      app.tabs[app.tabs.length - 1] = session;
      app.tabs.push(createsessiontab);
      app.tab = session.id;
    },
    onCloseClick(session) {
      const app = this;
      app.curSession = session;
      app.showSessionCloseConfirm = true;
    },
    onSessionCloseConfirmClose() {
      const app = this;
      app.curSession = null;
      app.showSessionCloseConfirm = false;
    },
    onSessionCloseConfirmSuccess() {
      const app = this;
      app.onSessionCloseConfirmClose();
      app.queryList();
    }
  }
}
</script>

<style scoped>

.q-tab--active {
  background: #21ba45 !important;
}

</style>
