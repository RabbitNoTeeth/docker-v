<template>
  <div style="width: 100%;height: 100%">
    <template v-if="id === 'new'">
      <div style="width: 100%;height: 100%">
        <div style="text-align: center">
          <h2>create a new session</h2>
        </div>
        <div class="row flex-center">
          <q-card class="my-card" @click="onWindowsClick">
            <q-card-section>
              <q-icon name="fab fa-windows" class="icon-windows"/>
            </q-card-section>
          </q-card>
          <q-card class="my-card" @click="onLinuxClick">
            <q-card-section>
              <q-icon name="fab fa-linux" class="icon-linux"/>
            </q-card-section>
          </q-card>
          <q-card class="my-card" @click="onMacosClick">
            <q-card-section>
              <q-icon name="fab fa-apple" class="icon-macos"/>
            </q-card-section>
          </q-card>
        </div>
      </div>
    </template>
    <template v-else>
      <div style="width: 100%;height: 100%">
        <q-splitter
          v-model="splitterModel"
          style="width: 100%;height: 100%"
        >

          <template v-slot:before>
            <q-tabs
              v-model="tab"
              vertical
              class="text-teal"
            >
              <q-tab name="Image" icon="fas fa-file-archive" label="Image" />
              <q-tab name="Container" icon="fas fa-box" label="Container" />
            </q-tabs>
          </template>

          <template v-slot:after>
            <q-tab-panels
              v-model="tab"
              animated
              swipeable
              vertical
              transition-prev="jump-up"
              transition-next="jump-up"
              style="width: 100%;height: 100%"
            >
              <q-tab-panel name="Image">
                <div class="text-h4 q-mb-md">Images</div>
                <image-list></image-list>
              </q-tab-panel>
              <q-tab-panel name="Container">
                <div class="text-h4 q-mb-md">Containers</div>
                <container-list></container-list>
              </q-tab-panel>
            </q-tab-panels>
          </template>

        </q-splitter>
      </div>
    </template>
    <session-add-form v-if="showForm" :os="os" @close="formClose" @success="formSuccess"></session-add-form>
  </div>
</template>

<script>
import SessionAddForm from "pages/main/SessionAddForm";
import ImageList from "pages/main/ImageList";
import ContainerList from "pages/main/ContainerList";

export default {
  name: "Session",
  components: {ContainerList, ImageList, SessionAddForm},
  data() {
    return {
      showForm: false,
      os: null,
      splitterModel: 10,
      tab: 'Image'
    }
  },
  props: {
    id: {
      type: String,
      default: null
    }
  },
  provide: function () {
    return {
      sessionId: this.id
    }
  },
  methods: {
    onWindowsClick() {
      const app = this;
      app.$q.notify({
        type: 'warning',
        position: 'center',
        multiLine: true,
        closeBtn: true,
        timeout: 30000,
        message: 'Support for Windows is in development...'
      });
    },
    onLinuxClick() {
      this.os = "LINUX";
      this.showForm = true;
    },
    onMacosClick() {
      const app = this;
      app.$q.notify({
        type: 'warning',
        position: 'center',
        multiLine: true,
        closeBtn: true,
        timeout: 30000,
        message: 'Support for MacOS is in development...'
      });
    },
    formClose() {
      this.os = null;
      this.showForm = false;
    },
    formSuccess(session) {
      this.formClose();
      this.$emit('add-session', session);
    },
  }
}
</script>

<style scoped>

.my-card {
  width: 100%;
  max-width: 250px;
  margin: 0 10px;
  text-align: center;
  background-color: #eeeeee;
}

.my-card:hover {
  cursor: pointer;
}

.my-card:hover .icon-windows:hover {
  color: #2196f3;
}

.icon-windows {
  color: #424242;
  font-size: 14em;
}

.my-card:hover .icon-linux:hover {
  color: #2196f3;
}

.icon-linux {
  color: #424242;
  font-size: 14em;
}

.my-card:hover .icon-macos:hover {
  color: #2196f3;
}

.icon-macos {
  color: #424242;
  font-size: 14em;
}

.q-tab--active {
  background: #e0e0e0 !important;
}

</style>
