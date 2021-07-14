<template>
  <div>
    <template v-if="id === 'new'">
      <div>
        <div style="text-align: center">
          <h2>create a new session</h2>
        </div>
        <div class="row flex-center">
          <q-card class="my-card">
            <q-card-section>
              <q-icon name="fab fa-windows" class="icon-windows"/>
            </q-card-section>
          </q-card>
          <q-card class="my-card">
            <q-card-section>
              <q-icon name="fab fa-linux" class="icon-linux"/>
            </q-card-section>
          </q-card>
          <q-card class="my-card">
            <q-card-section>
              <q-icon name="fab fa-apple" class="icon-macos"/>
            </q-card-section>
          </q-card>
        </div>
      </div>
    </template>
    <template v-else>
    </template>
  </div>
</template>

<script>
export default {
  name: "Session",
  props: {
    id: {
      type: String,
      default: null
    }
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

.my-card:hover{
  cursor: pointer;
}

.my-card:hover .icon-windows:hover{
  color: #2196f3;
}

.icon-windows {
  color: #424242;
  font-size: 14em;
}

.my-card:hover .icon-linux:hover{
  color: #2196f3;
}

.icon-linux {
  color: #424242;
  font-size: 14em;
}

.my-card:hover .icon-macos:hover{
  color: #2196f3;
}

.icon-macos {
  color: #424242;
  font-size: 14em;
}

</style>
