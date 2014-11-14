package com.avapir.roguelike.game.fov;

import java.awt.*;
import java.util.LinkedList;

/**
 * Precise permissive visibility algorithm.
 * <p>
 * TODO : Do multitile organism by replacing offsetT(0,1)(1, 0) by offsetT(0, size.y) (size.x, 0). Also need to
 * consider
 * border tiles.
 *
 * @author sdatta
 */
public class PermissiveFOV implements IFovAlgorithm {

    private class permissiveMaskT {
        /*
         * Do not interact with the members directly. Use the provided
         * functions.
         */ int north;
        int    south;
        int    east;
        int    west;
        int    distPlusOneSq;
        LosMap board;
    }

    // class offsetT
    // {
    // public offsetT(int newX, int newY)
    // {
    // x = newX;
    // y = newY;
    // }
    //
    // public int x;
    //
    // public int y;
    //
    // public String toString()
    // {
    // return "(" + x + ", " + y + ")";
    // }
    // }

    private class fovStateT {
        final boolean isLos = false;
        Point2I         source;
        permissiveMaskT mask;
        Point2I         quadrant;
        Point2I         extent;
        int             quadrantIndex;
        LosMap          board;
    }

    private class bumpT {
        Point2I location;
        bumpT parent = null;

        @Override
        public String toString() {
            return location.toString() + " p( " + parent + " ) ";
        }
    }

    class fieldT {
        Line2I steep   = new Line2I(new Point2I(0, 0), new Point2I(0, 0));
        Line2I shallow = new Line2I(new Point2I(0, 0), new Point2I(0, 0));
        bumpT steepBump;
        bumpT shallowBump;

        public fieldT(final fieldT f) {
            steep = new Line2I(new Point2I(f.steep.near.x, f.steep.near.y), new Point2I(f.steep.far.x, f.steep.far.y));
            shallow = new Line2I(new Point2I(f.shallow.near.x, f.shallow.near.y),
                                 new Point2I(f.shallow.far.x, f.shallow.far.y));
            steepBump = f.steepBump;
            shallowBump = f.shallowBump;
        }

        public fieldT() {}

        @Override
        public String toString() {
            return "[ steep " + steep + ",  shallow " + shallow + "]";
        }
    }

    private void calculateFovQuadrant(final fovStateT state) {
        final LinkedList<bumpT> steepBumps = new LinkedList<>();
        final LinkedList<bumpT> shallowBumps = new LinkedList<>();
        final LinkedList<fieldT> activeFields = new LinkedList<>();
        activeFields.addLast(new fieldT());
        activeFields.getLast().shallow.near = new Point2I(0, 1);
        activeFields.getLast().shallow.far = new Point2I(state.extent.x, 0);
        activeFields.getLast().steep.near = new Point2I(1, 0);
        activeFields.getLast().steep.far = new Point2I(0, state.extent.y);

        final Point2I dest = new Point2I(0, 0);

        if (state.quadrant.x == 1 && state.quadrant.y == 1) {
            actIsBlocked(state, dest);
        }

        CLikeIterator<fieldT> currentField = new CLikeIterator<>(activeFields.listIterator());
        int i;
        int j;
        final int maxI = state.extent.x + state.extent.y;
        for (i = 1; i <= maxI && !activeFields.isEmpty(); ++i) {
            final int startJ = max(0, i - state.extent.x);
            final int maxJ = min(i, state.extent.y);
            for (j = startJ; j <= maxJ && !currentField.isAtEnd(); ++j) {
                dest.x = i - j;
                dest.y = j;
                visitSquare(state, dest, currentField, steepBumps, shallowBumps, activeFields);
            }
            currentField = new CLikeIterator<>(activeFields.listIterator());
        }
    }

    private int max(final int i, final int j) {
        return i > j ? i : j;
    }

    private int min(final int i, final int j) {
        return i < j ? i : j;
    }

    private void visitSquare(final fovStateT state,
                             final Point2I dest,
                             final CLikeIterator<fieldT> currentField,
                             final LinkedList<bumpT> steepBumps,
                             final LinkedList<bumpT> shallowBumps,
                             final LinkedList<fieldT> activeFields) {
        final Point2I topLeft = new Point2I(dest.x, dest.y + 1);
        final Point2I bottomRight = new Point2I(dest.x + 1, dest.y);

        while (!currentField.isAtEnd() && currentField.getCurrent().steep.isBelowOrContains(bottomRight)) {
            // System.out.println("currFld.steep.isBelowOrContains(bottomRight) "
            // + currentField.getCurrent().steep
            // .isBelowOrContains(bottomRight));
            // case ABOVE
            // The square is in case 'above'. This means that it is ignored
            // for the currentField. But the steeper fields might need it.
            // ++currentField;
            currentField.gotoNext();
        }
        if (currentField.isAtEnd()) {
            // System.out.println("currentField.isAtEnd()");
            // The square was in case 'above' for all fields. This means that
            // we no longer care about it or any squares in its diagonal rank.
            return;
        }

        // Now we check for other cases.
        if (currentField.getCurrent().shallow.isAboveOrContains(topLeft)) {
            // case BELOW
            // The shallow line is above the extremity of the square, so that
            // square is ignored.
            // System.out.println("currFld.shallow.isAboveOrContains(topLeft) "
            // + currentField.getCurrent().shallow);
            return;
        }
        // The square is between the lines in some way. This means that we
        // need to visit it and determine whether it is blocked.
        final boolean isBlocked = actIsBlocked(state, dest);
        if (!isBlocked) {
            // We don't care what case might be left, because this square does
            // not obstruct.
            return;
        }

        if (currentField.getCurrent().shallow.isAbove(bottomRight) &&
                currentField.getCurrent().steep.isBelow(topLeft)) {
            // case BLOCKING
            // Both lines intersect the square. This getCurrentY field has ended.
            currentField.removeCurrent();
        } else if (currentField.getCurrent().shallow.isAbove(bottomRight)) {
            // case SHALLOW BUMP
            // The square intersects only the shallow line.
            addShallowBump(topLeft, currentField.getCurrent(), steepBumps, shallowBumps);
            checkField(currentField);
        } else if (currentField.getCurrent().steep.isBelow(topLeft)) {
            // case STEEP BUMP
            // The square intersects only the steep line.
            addSteepBump(bottomRight, currentField.getCurrent(), steepBumps, shallowBumps);
            checkField(currentField);
        } else {
            // case BETWEEN
            // The square intersects neither line. We need to split into two
            // fields.
            final fieldT steeperField = currentField.getCurrent();
            final fieldT shallowerField = new fieldT(currentField.getCurrent());
            currentField.insertBeforeCurrent(shallowerField);
            addSteepBump(bottomRight, shallowerField, steepBumps, shallowBumps);
            currentField.gotoPrevious();
            if (!checkField(currentField)) {
                currentField.gotoNext();// point to the original element
            }
            addShallowBump(topLeft, steeperField, steepBumps, shallowBumps);
            checkField(currentField);
        }
    }

    private boolean checkField(final CLikeIterator<fieldT> currentField) {
        // If the two slopes are colinear, and if they pass through either
        // extremity, remove the field of view.
        final fieldT currFld = currentField.getCurrent();
        boolean ret = false;

        if (currFld.shallow.doesContain(currFld.steep.near) && currFld.shallow.doesContain(currFld.steep.far) &&
                (currFld.shallow.doesContain(new Point2I(0, 1)) || currFld.shallow.doesContain(new Point2I(1, 0)))) {
            // System.out.println("removing "+currentField.getCurrent());
            currentField.removeCurrent();
            ret = true;
        }
        // System.out.println("CheckField "+ret);
        return ret;
    }

    private void addShallowBump(final Point2I point,
                                final fieldT currFld,
                                final LinkedList<bumpT> steepBumps,
                                final LinkedList<bumpT> shallowBumps) {
        // System.out.println("Adding shallow "+point);
        // First, the far point of shallow is set to the new point.
        currFld.shallow.far = point;
        // Second, we need to add the new bump to the shallow bump list for
        // future steep bump handling.
        shallowBumps.addLast(new bumpT());
        shallowBumps.getLast().location = point;
        shallowBumps.getLast().parent = currFld.shallowBump;
        currFld.shallowBump = shallowBumps.getLast();
        // Now we have too look through the list of steep bumps and see if
        // any of them are below the line.
        // If there are, we need to replace near point too.
        bumpT currentBump = currFld.steepBump;
        while (currentBump != null) {
            if (currFld.shallow.isAbove(currentBump.location)) {
                currFld.shallow.near = currentBump.location;
            }
            currentBump = currentBump.parent;
        }
    }

    private void addSteepBump(final Point2I point,
                              final fieldT currFld,
                              final LinkedList<bumpT> steepBumps,
                              final LinkedList<bumpT> shallowBumps) {
        // System.out.println("Adding steep "+point);
        currFld.steep.far = point;
        steepBumps.addLast(new bumpT());
        steepBumps.getLast().location = point;
        steepBumps.getLast().parent = currFld.steepBump;
        currFld.steepBump = steepBumps.getLast();
        // Now look through the list of shallow bumps and see if any of them
        // are below the line.
        bumpT currentBump = currFld.shallowBump;
        while (currentBump != null) {
            if (currFld.steep.isBelow(currentBump.location)) {
                currFld.steep.near = currentBump.location;
            }
            currentBump = currentBump.parent;
        }
    }

    private boolean actIsBlocked(final fovStateT state, final Point2I pos) {
        final Point2I adjustedPos = new Point2I(pos.x * state.quadrant.x + state.source.x,
                                                pos.y * state.quadrant.y + state.source.y);

        if (!state.board.hasTile(adjustedPos.x, adjustedPos.y)) {
            return false;// we are getting
            // outside the board
        }

        // System.out.println("actIsBlocked "+adjustedPos.x+" "+adjustedPos.y);

        // if ((state.quadrant.x * state.quadrant.y == 1
        // && pos.x == 0 && pos.y != 0)
        // || (state.quadrant.x * state.quadrant.y == -1
        // && pos.y == 0 && pos.x != 0)
        // || doesPermissiveVisit(state.mask, pos.x*state.quadrant.x,
        // pos.y*state.quadrant.y) == 0)
        // {
        // // return result;
        // }
        // else
        // {
        // board.visit(adjustedPos.x, adjustedPos.y);
        // // return result;
        // }
        /*
         * ^ | 2 | <-3-+-1-> | 4 | v
		 * 
		 * To ensure all squares are visited before checked ( so that we can
		 * decide obstacling at visit time, eg walls destroyed by explosion) ,
		 * visit axes 1,2 only in Q1, 3 in Q2, 4 in Q3
		 */
        if (state.isLos // In LOS calculation all visits allowed
                || state.quadrantIndex == 0 // can visit anything from Q1
                || (state.quadrantIndex == 1 && pos.x != 0) // Q2 : no getCurrentY axis
                || (state.quadrantIndex == 2 && pos.y != 0) // Q3 : no getCurrentX axis
                || (state.quadrantIndex == 3 && pos.x != 0 && pos.y != 0)) {
            // no getCurrentX
            // or getCurrentY
            // axis
            if (doesPermissiveVisit(state.mask, pos.x * state.quadrant.x, pos.y * state.quadrant.y) == 1) {
                state.board.visit(adjustedPos.x, adjustedPos.y);
            }
        }
        return state.board.isObstacle(adjustedPos.x, adjustedPos.y);
    }

    private void permissiveFov(final Point p, final permissiveMaskT mask) {
        final fovStateT state = new fovStateT();
        state.source = new Point2I(p);
        state.mask = mask;
        state.board = mask.board;
        // state.isBlocked = isBlocked;
        // state.visit = visit;
        // state.context = context;

        final int quadrantCount = 4;
        final Point2I quadrants[] = {new Point2I(1, 1), new Point2I(-1, 1), new Point2I(-1, -1), new Point2I(1, -1)};

        final Point2I extents[] = {new Point2I(mask.east, mask.north), new Point2I(mask.west, mask.north), new Point2I(
                mask.west, mask.south), new Point2I(mask.east, mask.south)};
        int quadrantIndex = 0;
        for (; quadrantIndex < quadrantCount; ++quadrantIndex) {
            state.quadrant = quadrants[quadrantIndex];
            state.extent = extents[quadrantIndex];
            state.quadrantIndex = quadrantIndex;
            calculateFovQuadrant(state);
        }
    }

    private int doesPermissiveVisit(final permissiveMaskT mask, final int x, final int y) {
        if (x * x + y * y < mask.distPlusOneSq) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public void visitFieldOfView(final LosMap b, final Point p, final int distance) {
        final permissiveMaskT mask = new permissiveMaskT();
        mask.east = mask.north = mask.south = mask.west = distance;
        mask.distPlusOneSq = (distance + 1) * (distance + 1);
        mask.board = b;
        permissiveFov(p, mask);
    }

}
