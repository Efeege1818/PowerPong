# Console UI – FourConnect Toxic

This is a Snake-style ASCII console renderer for the FourConnect Toxic game.

## Symbols

- . = empty field
- R = red chip
- Y = yellow chip
- t = toxic field
- r = red chip on toxic
- y = yellow chip on toxic

## Rendering

The console UI renders the current GameBoard state by reading:
- Field.isOccupied()
- Field.getOccupyingPlayer()
- Field.isToxicZone()

## Notes

This UI contains only presentation logic and does not implement game rules.
