package edu.illinois.cs.cs125.spring2020.mp.logic;

/*
 * Welcome to the MACHINE PROJECT!
 *
 * Most of your work in Checkpoint 0 will be done in this file. Everything here has to do with
 * target mode logic. For general information on how we keep track of the game, see the big comment
 * below this one (the "class Javadoc"). For each function, read the comment above it to see what
 * you need to do, then write code inside the function's curly braces to implement that logic. At
 * first all the functions return default values. We just added these return statements so the
 * project can compile; you'll want to replace the return statements to return the actual result you
 * determined.
 *
 * Once you've finished everything here - passed all the tests except testTargetModeGameplay - move
 * on to GameActivity to make the game work!
 */

import com.google.android.gms.maps.model.LatLng;

/**
 * Holds methods for managing a path of target claims.
 * <p>
 * The set of locations is represented as a pair of identically sized arrays. The first target's latitude
 * is the first entry in the latitudes array; its longitude is the first entry in the longitudes array.
 * The path array contains indexes of the captured targets in the order they were captured. To ensure
 * that's it's possible to capture all targets, the path array has the same size as the coordinate arrays.
 * If not all targets have been captured yet, unused slots of the path array have the value -1. The path
 * array is filled starting at the beginning, so all instances of -1 are contiguous at the end of the array.
 * <p>
 * For example, if latitudes is [40.2, 40.8, 40.5], longitudes is [-88.5, -88.3, -88.0], and path is [1, -1, -1],
 * there are three targets: (40.2, -88.5), (40.8, -88.3), and (40.5, -88.0). The target at index 1, that is,
 * (40.8, -88.3), has been captured; the other two have not. If path were [-1, -1, -1], that would indicate
 * that the user has captured no targets so far. If path were [0, 1, -1], that would indicate that the user
 * first captured target #0, that is, (40.2, -88.5), and then captured target #1.
 * <p>
 * You need to complete the functions here. You can then use them to implement the game in
 * the location update handler of GameActivity!
 */
public class TargetVisitChecker {

    /**
     * Tag for logging. This allows you to use Android's logging feature like so:
     * Log.i(TAG, "Your log message");
     * <p>
     * Alternatively you can use the standard System.out.println - up to you.
     */
    private static final String TAG = "TargetVisitChecker";

    /**
     * Determines whether the specified target is within range of the current location.
     * Once this is done, you can use it to implement getVisitCandidate.
     * <p>
     * It is assumed that all parameters are valid. The latitudes and longitudes arrays are the same size.
     * All coordinates are valid. The range is positive.
     * <p>
     * This function should not modify the arrays it receives.
     *
     * @param latitudes        the latitudes of all targets in the game
     * @param longitudes       the longitudes of all targets in the game (same order as latitudes)
     * @param targetIndex      the index (into the coordinate arrays) of the target to check for proximity
     * @param currentLatitude  the player's current latitude
     * @param currentLongitude the player's current longitude
     * @param range            the proximity threshold
     * @return whether the target at the specified index is within range of the current location
     */
    public static boolean isTargetWithinRange(final double[] latitudes, final double[] longitudes,
                                              final int targetIndex,
                                              final double currentLatitude, final double currentLongitude,
                                              final int range) {
        // HINT: To find the distance in meters between two locations, use a provided helper function:
        // LatLngUtils.distance(oneLatitude, oneLongitude, otherLatitude, otherLongitude)
        if (LatLngUtils.distance(currentLatitude, currentLongitude,
                latitudes[targetIndex], longitudes[targetIndex]) > range) {
            return false;
        }
        return true;
    }


    /**
     * Determines whether a target is already visited.
     * Once this is done, you can use it to implement getVisitCandidate.
     * <p>
     * A target is visited if its index appears in the path array.
     * <p>
     * It is assumed that the index is valid and path is non-null.
     * This function should not modify the array it receives.
     *
     * @param path        indexes of targets visited so far, in the order they were visited (-1 for empty slots)
     * @param targetIndex the index of the target to check for visitedness
     * @return whether the specified target is already visited
     */
    public static boolean isTargetVisited(final int[] path, final int targetIndex) {
        // HINT: The user can capture targets in many different orders. Target #0 is not necessarily captured first.
        for (int index = 0; index < path.length; index++) {
            if (targetIndex == path[index]) {
                return true;
            }
        }
        return false;

    }

    /**
     * Gets an index of an unvisited target within the specified range of the current location.
     * You will find the above functions, isTargetWithinRange and isTargetVisited, useful for
     * implementing this one.
     * <p>
     * It is assumed that all parameters are valid. The arrays are non-null and of the same length.
     * All coordinates are valid. The range is positive.
     * <p>
     * This function should not modify the arrays it receives.
     *
     * @param latitudes        the latitudes of all targets
     * @param longitudes       the longitudes of all targets (same order as latitudes)
     * @param path             indexes of targets visited so far (same size as latitudes, -1 for empty slots)
     * @param currentLatitude  the current latitude
     * @param currentLongitude the current longitude
     * @param range            maximum distance to target, in meters
     * @return the index of a target within the range that is not on the path, or -1 if no such target exists
     */
    public static int getVisitCandidate(final double[] latitudes, final double[] longitudes, final int[] path,
                                        final double currentLatitude, final double currentLongitude,
                                        final int range) {
        // HINT: Implement isTargetWithinRange and isTargetVisited (above) first.
        // Then you can call them in this function.
        for (int index = 0; index < path.length; index++) {
            if (isTargetWithinRange(latitudes, longitudes, index, currentLatitude, currentLongitude, range) == true) {
                if (isTargetVisited(path, index) == false) {
                    return index;
                }
            }

        }
        return -1;
    }

    /**
     * Determines whether the specified target can be visited without violating the snake rule.
     * <p>
     * The snake rule is violated if the new line created between the last captured target and this
     * new target would cross the straight line connecting two sequentially captured targets. For example,
     * there is a line between the first-captured and second-captured targets, and between the second-captured
     * and third-captured targets, but no line directly connecting the first-captured and third-captured
     * targets. If zero or one targets have been captured so far, there are no lines and it is permissible
     * to capture any target.
     * <p>
     * It is assumed that all parameters are valid. The three arrays are non-null and of the same length.
     * All coordinates are valid. The index of the target to visit is a valid index into the coordinate
     * arrays. The index of the target to visit does not appear in the path array (i.e. the target has not
     * been visited yet). No existing lines violate the snake rule.
     * <p>
     * This function should not modify the arrays it receives.
     *
     * @param latitudes  latitudes of all targets
     * @param longitudes longitudes of all targets (same order as latitudes)
     * @param path       indexes of targets visited so far (same size as latitudes, -1 for empty slots)
     * @param tryVisit   index of the target to try to visit
     * @return whether the target can be claimed
     */
    public static boolean checkSnakeRule(final double[] latitudes, final double[] longitudes,
                                         final int[] path, final int tryVisit) {
        // HINT: To determine whether two lines cross, use a provided helper function:
        // LineCrossDetector.linesCross(oneStartLat, oneStartLng, oneEndLat, oneEndLng,
        //                              otherStartLat, otherStartLng, otherEndLat, otherEndLng)
        int finalindex = 0;
        while (path[finalindex] != -1) {
            finalindex += 1;
        }
        for (int index = 0; index < finalindex - 1; index++) {
            LatLng finalMinusOne = new LatLng(latitudes[path[finalindex - 1]], longitudes[path[finalindex - 1]]);
            LatLng atTryVisit = new LatLng(latitudes[tryVisit], longitudes[tryVisit]);
            LatLng atIndex = new LatLng(latitudes[path[index]], longitudes[path[index]]);
            LatLng atIndexPlusOne = new LatLng(latitudes[path[index + 1]], longitudes[path[index + 1]]);
            if (LineCrossDetector.linesCross(finalMinusOne, atTryVisit, atIndex, atIndexPlusOne)) {
                return false;
            }
        }
        return true;

    }

    /**
     * Marks a target captured by putting its index in the first available (-1) slot of the path array.
     * <p>
     * It is assumed that all parameters are valid. The path array is non-null and does not yet contain
     * the target index. The target index is non-negative.
     *
     * @param path        the path array
     * @param targetIndex the target being visited
     * @return the index in the path array that was updated, or -1 if the path array was full
     */
    public static int visitTarget(final int[] path, final int targetIndex) {
        // HINT: The return value of this function will be useful in GameActivity.
        int finalIndex = -1;
        for (int i = 0; i < path.length; i++) {
            if (path[i] == -1) {
                path[i] = targetIndex;
                finalIndex = i;
                break;
            }
        }
        return finalIndex;
    }
}

