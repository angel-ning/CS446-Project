package com.example.spaceshare.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.spaceshare.ui.viewmodel.MessagesViewModel
import com.example.spaceshare.R
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spaceshare.adapters.ChatAdapter
import com.example.spaceshare.data.repository.UserRepository
import com.example.spaceshare.databinding.FragmentMessagesBinding
import com.example.spaceshare.models.Chat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MessagesFragment : Fragment() {

    companion object {
        val tag = this::class.simpleName
    }

    @Inject
    lateinit var messagesViewModel: MessagesViewModel

    @Inject
    lateinit var userRepo : UserRepository

    private lateinit var adapter: ChatAdapter

    private lateinit var navController: NavController
    private lateinit var binding: FragmentMessagesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_messages, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = requireActivity().findNavController(R.id.main_nav_host_fragment)

        configureRecyclerView()
        configureChatObservers()
        configureButtons()
    }

    private fun configureButtons() {
        binding.refreshButton.setOnClickListener {
            messagesViewModel.fetchChats()
        }
    }

    private fun configureRecyclerView() {
        adapter = ChatAdapter(userRepo, object : ChatAdapter.ItemClickListener {
            override fun onItemClick(chat: Chat) {
                val chatDialogFragment = ChatDialogFragment(chat, shouldRefreshChatsList = true)
                chatDialogFragment.show(
                    childFragmentManager,
                    "chatDialog"
                )
            }
        })
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun configureChatObservers() {
        messagesViewModel.chats.observe(viewLifecycleOwner) { chats ->
            adapter.submitList(chats)
        }
    }
}