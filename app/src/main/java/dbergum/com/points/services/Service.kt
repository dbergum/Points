package dbergum.com.points.services

import com.firebase.client.Firebase

/**
 * Created by David on 11/23/2015.
 */
abstract class Service {
    protected final val firebase: Firebase = Firebase("https://intense-heat-3913.firebaseio.com");
}