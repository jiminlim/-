<template>
 <v-card-text>
   <hr>
    <v-col class="row text-center mx-auto">
      <v-text-field
        label="댓글을 작성해주세요."
        single-line
        v-model="commentData.content" 
        @keypress.enter="commentCreate"
      ></v-text-field>
      <v-btn icon class="d-flex align-self-center">
          <i class="far fa-paper-plane fa-lg my-auto" @click="commentCreate"></i>
      </v-btn>
    </v-col>
    <div v-for="comment in comments" :key="comment.id">
      <CommentListItem class="mb-3" @delete-comment2="commentDelete" :comment="comment" :currentUser="currentUser"/>
    </div>
    <br>
    <!-- <p class="text-center" v-if="!comments.length">No results :( </p> -->
    <infinite-loading  @infinite="infiniteHandler"></infinite-loading>
  </v-card-text>
</template>

<script>
import axios from 'axios'
import CommentListItem from '@/components/community/CommentListItem'
import InfiniteLoading from 'vue-infinite-loading'
import SERVER from '@/api/drf'

export default {
  name: 'CommentList',
  components: {
    CommentListItem,
    InfiniteLoading,
  },
  props:{
    commentCnt:Number,
    communityIdx:Number
  },
  data(){
    return {
      currentUser:{},
      comments: [],
      commentData: {
        content: '',
      },
      page: 1,
    }
  },
  methods: {
    checkAuth(){
      const axiosConfig ={
          headers:{
              token : `${this.$cookies.get('auth-token')}`
          },
      }
      axios.get(SERVER.URL+`/user/getUserInfo`,axiosConfig)
      .then((reaponse)=>{
          this.currentUser = reaponse.data
      })
      .catch((err)=>{
          console.error(err)
      })
    },

    getComments(){
        const axiosConfig2 = {
          headers:{
            token: `${this.$cookies.get('auth-token')}`,
            },
          params: {co_idx:this.$route.params.communityId, page: this.page}
        }
        axios.get(SERVER.URL+`/community/detail/replylist/${this.$route.params.communityId}`,axiosConfig2)
        .then((response)=>{
          this.comments.push(...response.data)
        })
        .catch((err)=>{
            console.error(err)
        })
    },


    infiniteHandler($state) {
      const axiosConfig2 = {
        headers:{
          token: `${this.$cookies.get('auth-token')}`,
          },
        params: {co_idx:this.$route.params.communityId, page: this.page+1}
      } 
      if (parseInt(this.commentCnt / 5)+1 >= this.page){
        axios.get(SERVER.URL +`/community/detail/replylist/${this.$route.params.communityId}`, axiosConfig2)
          .then(res => {
            setTimeout(() => {
              this.page+=1
              this.comments.push(...res.data)
              $state.loaded()
            }, 1000);
          })
          .catch(err => console.log(err))
      } else{
        $state.complete()     
      }
    },


    commentCreate(){
        const json = {
            co_idx : this.$route.params.communityId ,
            cr_content : this.commentData.content
        }
        const axiosConfig2 = {
            headers:{
                token : `${this.$cookies.get('auth-token')}`
            },
            params: {page: this.page}
        }
      if (!this.commentData.content.trim()){
        this.$alert('내용을 작성해주세요')
        this.commentData.content =''
      } else{
        if (!this.$cookies.isKey('auth-token')){
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
            this.commentData.content = ''
        } else{
-          axios.post(SERVER.URL + `/community/replyadd`,json,axiosConfig2)
           .then((res) => {
              this.$emit('add-comment')
              this.commentData.content = ''
              this.comments = []
              this.comments.push(...res.data)
            })
            .catch((err) => { console.log(err.response.data) })
        }
      }
    },


    commentDelete(idx){
      const axiosConfig2 = {
        headers:{
          token: `${this.$cookies.get('auth-token')}`,
          },
        params: {page: this.page}
      }
        axios.delete(SERVER.URL+`/community/replydelete/${this.$route.params.communityId}/${idx}`,axiosConfig2)
        .then((response)=>{
            this.$emit('delete-comment')
            this.comments = []
            this.comments.push(...response.data)
            this.$alert('삭제 완료')
        })
        .catch((err)=>{
            console.log(err)
        })
     },
  },
  
  created(){
    this.getComments()
    this.checkAuth()
  }
}
</script>

<style>

</style>