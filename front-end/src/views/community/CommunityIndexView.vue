<template>
  <div>
      <div class="d-flex flex-row-reverse px-4 mb-5">
        <v-btn rounded 
                    
          color="pink" 
          dark
          class="mt-4" 
          @click="createCommunity">
          <div class="mx-2"><v-icon class="mr-2">mdi-pencil</v-icon>게시글 작성</div>   
        </v-btn>
      </div>
      <v-tabs
          color="pink"
          class="d-flex justify-center">
          <v-tab @click="getRecentCommunity" ><h4>최신 순으로 보기</h4></v-tab>
          <v-tab @click="getLikeCommunity" ><h4>인기 순으로 보기</h4></v-tab>
      </v-tabs>
      <br>
      <div class="mt-5">
        <div v-for="community in communities" :key="community.id">
            <CommunityDetailItem :community="community" :page="page" @community-delete="deleteArticle"/>
        </div>
        <div  class="mt-5 my-10 text-center">
          <p class="mt-2" v-if="!communities.length">No results :(</p>
          <infinite-loading v-if="communities.length" @infinite="infiniteHandler" spinner="waveDots"></infinite-loading>
        </div>
      </div>    
    <transition name="fade">
      <ScrollTopButton v-if="scrolled"/>
    </transition>    
  </div>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import axios from 'axios'
import CommunityDetailItem from '@/components/community/CommunityDetailItem.vue'
import InfiniteLoading from 'vue-infinite-loading'
import SERVER from '@/api/drf'

import ScrollTopButton from '@/components/main/ScrollTopButton'

export default {
    name: 'CommunityIndexView',
    components: {
      CommunityDetailItem,
      InfiniteLoading,
      ScrollTopButton,
    },  
    data(){
      return{
        communities:[],
        page: 1,
        no:1,
        communityNum:null,
        changeFlag:true,
        scrolled: false,
      }
    },
    computed: {
      ...mapGetters(['isLoggedIn']),
    },
    methods:{
      ...mapActions(['getUser']),
      checkLogin(){
            if (!(this.$cookies.get('auth-token'))){
              this.getUser()
              this.$confirm(
                  {
                  message: `로그인 해주세요.`,
                  button: {
                      yes: '로그인 하기',
                      no: '돌아가기',
                  },
                  callback: confirm => {
                      if (confirm) {
                        this.$router.push({ name: 'Login'})
                      }
                  }})
              this.$router.push({name:'Home'})                
            }
        },
      createCommunity(){
            this.$router.push({ name: 'CommunityCreateView' })
          },

      getLikeCommunity(){
            this.communities = []
            this.no = 1
            this.page = 1
            const axiosConfig = {
              headers:{
                token: `${this.$cookies.get('auth-token')}`,
                },
              params: {no: this.no, page: this.page}
            }
              axios.get(SERVER.URL + `/community/list`,axiosConfig)
                .then((response) => {
                  this.communityNum = response.data[0].co_cnt
                  this.communities.push(...response.data)
                })
                .catch((err) => {console.log(err)})
            },

      getRecentCommunity(){
            this.communities = []
            this.no = 2
            this.page = 1
            const axiosConfig = {
              headers:{
                token: `${this.$cookies.get('auth-token')}`,
                },
              params: {no: this.no, page: this.page}
            }
              axios.get(SERVER.URL + `/community/list`,axiosConfig)
                .then((response) => {
                  this.communityNum = response.data[0].co_cnt
                  this.communities.push(...response.data)
                })
                .catch((err) => {
                  console.log(err)
                  })
            },

      infiniteHandler($state){
            const axiosConfig = {
              headers:{
                token: `${this.$cookies.get('auth-token')}`,
                },
              params: {no: this.no, page: this.page+1}
            }
            if (parseInt(this.communityNum / 5) + 1 >= this.page){
              axios.get(SERVER.URL +`/community/list`, axiosConfig)
                .then(res => {
                  setTimeout(() => {
                    this.page += 1
                    this.communities.push(...res.data)
                    $state.loaded()
                  }, 1000);
                })
                .catch(err => console.log(err))
            } else{
              $state.complete()     
            }
          },

        deleteArticle(idx){
            const axiosConfig2 = {
              headers:{
                token: `${this.$cookies.get('auth-token')}`,
                },
              params: {co_idx:idx ,no: this.no, page: this.page}
            }
            axios.delete(SERVER.URL+`/community/delete/${idx}`,axiosConfig2)
            .then((response)=>{
                this.communities=[]
                this.communities.push(...response.data)
                this.$alert('삭제 완료')
            })
            .catch((err)=>{
                console.log(err)
            })
        },
        detectWindowScrollY() {
          this.scrolled = window.scrollY > 0
        }

    },
      created(){
        this.checkLogin()
        if (this.isLoggedIn) {
          this.getRecentCommunity()
        }
      },
      mounted() {
        window.addEventListener('scroll', this.detectWindowScrollY)
        },
      beforeDestory() {
        window.removeEventListener('scroll', this.detectWindowScrollY)
      }
    }
</script>

<style>

</style>