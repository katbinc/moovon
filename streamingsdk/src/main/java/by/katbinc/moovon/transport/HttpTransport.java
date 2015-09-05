package by.katbinc.moovon.transport;

import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

import by.katbinc.moovon.api.Api;
import by.katbinc.moovon.exception.ApiDataNotFoundException;
import by.katbinc.moovon.model.NavigationContentModel;
import by.katbinc.moovon.model.NavigationItemModel;
import by.katbinc.moovon.model.NavigationModel;
import by.katbinc.moovon.model.PlayerModel;
import by.katbinc.moovon.model.PlayerStreamModel;
import by.katbinc.moovon.model.TrackInfoModel;
import by.katbinc.moovon.utils.UriUtil;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class HttpTransport {

    public Context mContext;

    public HttpTransport(Context context) {
        mContext = context;
    }

    public void loadPlayerStreams(final OnStreamLoadListener listener) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Api.URL_API).build();

        final Api api = restAdapter.create(Api.class);
        api.getNavigationData(new Callback<NavigationModel>() {
            @Override
            public void success(NavigationModel model, Response response) {
                try {
                    if (response.getStatus() != 200) {
                        throw new ApiDataNotFoundException();
                    }
                    int playerId = findPlayerId(model);

                    api.getPlayerData(playerId, new Callback<PlayerModel>() {
                        @Override
                        public void success(PlayerModel playerModel, Response response) {
                            listener.onSuccess(getStreamsFiltered(playerModel.getStreams()));
                        }

                        @Override
                        public void failure(RetrofitError e) {
                            listener.onError(e);
                        }
                    });
                } catch (ApiDataNotFoundException e) {
                    listener.onError(e);
                }
            }

            @Override
            public void failure(RetrofitError e) {
                listener.onError(e);
            }
        });

    }

    private int findPlayerId(NavigationModel navigationModel) throws ApiDataNotFoundException {
        for (NavigationItemModel item : navigationModel.getNavigationItems()) {
            try {
                NavigationContentModel content = item.getContent();
                if (content.getType().equals(Api.KEY_PLAYER)) {
                    return content.getId();
                }
            } catch (Exception e) {
                // ignore
            }
        }
        throw new ApiDataNotFoundException();
    }

    /**
     * Filter stream objects by streamUrl not empty field
     *
     */
    private ArrayList<PlayerStreamModel> getStreamsFiltered(ArrayList<PlayerStreamModel> streams) {
        ArrayList<PlayerStreamModel> filtered = new ArrayList<>();
        for (PlayerStreamModel stream : streams) {
            try {
                if (!stream.getStreamUrl().isEmpty()) {
                    try {
                        stream.getCover().setSource(Api.URL_API + "/" + stream.getCover().getSource());
                        stream.getCoverBig().setSource(Api.URL_API + "/" + stream.getCoverBig().getSource());
                    } catch(NullPointerException e) {
                        // ignore
                    }
                    filtered.add(stream);
                }
            } catch (NullPointerException e) {
                // ignore
            }
        }
        return filtered;
    }

    public void loadTrackInfo(String url, final OnTrackInfoLoadListener listener) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(UriUtil.getBase(url)).build();

        final Api api = restAdapter.create(Api.class);
        Map<String, String> params = UriUtil.getParameters(url);
        api.getTrackInfo(params, new Callback<Response>() {
            @Override
            public void success(Response result, Response response) {
                String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                listener.onSuccess(new TrackInfoModel().loadFromString(responseStr));
            }

            @Override
            public void failure(RetrofitError e) {
                listener.onError(e);
            }
        });

    }

    public static String getTrackSrcUrl(String streamUrl) {
        int pos = streamUrl.indexOf("?");
        return streamUrl.substring(0, pos - 1) + Api.URL_PART_TRACK_SRC + streamUrl.substring(pos);
    }

    public interface OnStreamLoadListener {
        void onSuccess(ArrayList<PlayerStreamModel> streams);
        void onError(Exception e);
    }

    public interface OnTrackInfoLoadListener {
        void onSuccess(TrackInfoModel trackInfo);
        void onError(Exception e);
    }
}
