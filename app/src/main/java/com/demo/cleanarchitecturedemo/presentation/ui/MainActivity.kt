package com.demo.cleanarchitecturedemo.presentation.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.cleanarchitecturedemo.R
import com.demo.cleanarchitecturedemo.domain.model.Post
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: PostsViewModel by viewModel()
    private lateinit var postsAdapter: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize Views
       val recyclerView:RecyclerView = findViewById(R.id.list_of_post_rv)
       val progressBar:ProgressBar = findViewById(R.id.progressBar)

        // Setup RecyclerView
        postsAdapter = PostsAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = postsAdapter
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                // Handle Loading Visibility
                if (state.isLoading) {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }

                // Handle Data
                if (state.posts.isNotEmpty()) {
                    postsAdapter.updateData(state.posts)
                    Log.d("Posts", "Loaded ${state.posts.size} posts")
                }

                // Handle Error
                if (state.error != null) {
                    Log.e("Error", state.error)
                    Toast.makeText(this@MainActivity, state.error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    class PostsAdapter(private var posts: List<Post> = emptyList()) :
        RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

        fun updateData(newPosts: List<Post>) {
            val diffCallback = PostDiffCallback(posts, newPosts)
            val diffResult = androidx.recyclerview.widget.DiffUtil.calculateDiff(diffCallback)
            posts = newPosts
            diffResult.dispatchUpdatesTo(this)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.custom_post_row, parent, false)
            return PostViewHolder(view)
        }

        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
            holder.bind(posts[position])
        }

        override fun getItemCount(): Int = posts.size

        class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val title: TextView = itemView.findViewById(R.id.title_tv)
            private val body: TextView = itemView.findViewById(R.id.body_tv)

            fun bind(post: Post) {
                title.text = post.title
                body.text = post.body
            }
        }
    }

   class PostDiffCallback(
        private val oldList: List<Post>,
        private val newList: List<Post>
    ) : androidx.recyclerview.widget.DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }


}