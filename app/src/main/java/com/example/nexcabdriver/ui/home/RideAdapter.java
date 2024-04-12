package com.example.nexcabdriver.ui.home;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nexcabdriver.R;
import com.example.nexcabdriver.models.Ride;

import java.util.List;

public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {

    private List<Ride> rideList;
    private OnAcceptClickListener onAcceptClickListener;

    public RideAdapter(List<Ride> rideList) {

        this.rideList = rideList;


    }


    // Setter method for OnAcceptClickListener
    public void setOnAcceptClickListener(OnAcceptClickListener listener) {
        this.onAcceptClickListener = listener;
    }

    public interface OnAcceptClickListener {
        void onAcceptClick(Ride ride);

    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);
        holder.bind(ride);
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public class RideViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewRideDetails;
        private Button acceptButton;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRideDetails = itemView.findViewById(R.id.rideDetailsTextView);
            acceptButton = itemView.findViewById(R.id.acceptButton);

            // expand the list item when clicked
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(acceptButton.getVisibility() == View.VISIBLE){
                        acceptButton.setVisibility(View.GONE);
                    }else{
                        acceptButton.setVisibility(View.VISIBLE);
                    }
                }
            });

            // pass the ride object when clicked accept
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("accept button", "Inside Onclick!!");
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onAcceptClickListener != null) {
                        Ride clickedRide = rideList.get(position);
                        onAcceptClickListener.onAcceptClick(clickedRide);
                    }
                }
            });
        }

        public void bind(Ride ride) {
            textViewRideDetails.setText(getRideDetails(ride));
            // Bind other ride details to respective views
        }
    }

    public String getRideDetails(Ride ride){
        String result = "Pickup Location: "+ride.getPickupLocation()+"\n";
        result += "Dropoff Location: "+ride.getDropoffLocation()+"\n";
        result += "Date: "+ride.getDate()+"\n";
        result += "Time: "+ (ride.getTime());

        return result;
    }

//    public static String roundOffToNearestMinute(String time) {
//        if (time == null) {
//            return "";
//        }
//        String[] parts = time.split(":"); // Split time string by colon
//        int hour = Integer.parseInt(parts[0]); // Extract hour component
//        int minute = Integer.parseInt(parts[1]); // Extract minute component
//        return String.format("%02d:%02d", hour, minute); // Format and return rounded time
//    }
}
