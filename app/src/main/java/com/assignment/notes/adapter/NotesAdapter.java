package com.assignment.notes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.assignment.notes.R;
import com.assignment.notes.model.NoteModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class NotesAdapter extends FirestoreRecyclerAdapter<NoteModel, NotesAdapter.NotesViewHolder> {

    Context context;
    NoteListClickListener noteListClickListener;

    public NotesAdapter(@NonNull FirestoreRecyclerOptions<NoteModel> options, Context context, NoteListClickListener noteListClickListener) {
        super(options);
        this.context=context;
        this.noteListClickListener=noteListClickListener;
    }

    @Override
    protected void onBindViewHolder(@NonNull NotesViewHolder holder, int position, @NonNull NoteModel noteModel) {

        String docID=this.getSnapshots().getSnapshot(position).getId();
        holder.noteTitle.setText(noteModel.getTitle());
        holder.noteContent.setText(noteModel.getContent());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteListClickListener.onClickItem(docID, noteModel.getTitle(), noteModel.getContent());
            }
        });
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout, parent, false);
        return new NotesViewHolder(view);
    }

    /*

    @NonNull
    @Override
    public NotesAdapter.NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.notes_layout ,parent,false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.NotesViewHolder holder, int position) {
        NoteModel noteModel=notesList.get(position);

        holder.noteTitle.setText(noteModel.getTitle());
        holder.noteContent.setText(noteModel.getContent());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }*/

    public static class NotesViewHolder extends RecyclerView.ViewHolder{

        TextView noteTitle, noteContent;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle= itemView.findViewById(R.id.note_title);
            noteContent=itemView.findViewById(R.id.note_content);
        }
    }

    public interface NoteListClickListener{
        void onClickItem(String docID,String noteTitle, String noteContent);
    }
}
