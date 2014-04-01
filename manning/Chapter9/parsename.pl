#!perl -w

while( <> ) {
  print;
  chop;

  # Save this for later
  my $line = $_;

  if (/^\s*         # Ignore any whitespace at
                    # the start of the line
      (M(s|r|rs)\.) # Match Ms., Mrs., and Mr. (titles)
      \s+           # Space between title and first name
      (\S+)         # First name
      \s+           # Space between first name and last name
      (\S+)         # Last name
      \s*$          # Allow whitespace, but nothing else,
                    # after name
      /ix) {

    my $title = $1;
    my $firstName = $3;
    my $lastName = $4;

    print "  Title: $title\n";
    print "  First Name: $firstName\n";
    print "  Last Name: $lastName\n";

    my $modernLine = &modernize( $line );
    if ($modernLine ne $line) {
      print "  Modernized: $modernLine\n";
    }
  } else {
    print "  (Doesn't match!)\n";
  }
}

sub modernize {
  my ($line) = @_;

  $line =~ s/(?<=m)rs\./s./i;

  return $line;
}
