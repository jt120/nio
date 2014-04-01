#!perl -w

while( <> ) {
  chop;

  @words = split( /\s*:\s*/ );

  # -1 means we haven't found a word yet
  my $longest = -1;
  my $longestLength = 0;
  for ($i=0; $i<@words; ++$i) {
    my $length = length $words[$i];
    if ($length > $longestLength) {
      $longest = $i;
      $longestLength = $length;
    }
  }

  print "$words[$longest]\n";
}
