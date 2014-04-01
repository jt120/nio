#!perl -w

my @vec = ();

while( <> ) {
  chop;

  @words = split( /\s+/ );

  if ($words[0] eq "moveto") {
    # Grab the arguments from the word list
    my $x = $words[1];
    my $y = $words[2];

    # Call the routine that implements the command
    &moveTo( $x, $y );
  } elsif ($words[0] eq "setname") {
    # Grab the arguments from the word list
    my $name = $words[1];

    # Call the routine that implements the command
    &setName( $name );
  } elsif ($words[0] eq "setbounds") {
    # Grab the arguments from the word list
    my $x = $words[1];
    my $y = $words[2];
    my $w = $words[3];
    my $h = $words[4];

    # Call the routine that implements the command
    &setBounds( $x, $y, $w, $h );
  } else {
    # Any unknown command, and we quit
    die "Error: $_";
  }
}

sub moveTo {
  my ($x, $y) = @_;
  print "- moveTo $x $y\n";

}

sub setName {
  my ($name) = @_;
  print "- setName $name\n";
}

sub setBounds {
  my ($x, $y, $w, $h) = @_;
  print "- setBounds $x $y $w $h\n";
}
